package com.legacy_lens.service.impl;

import com.legacy_lens.dto.request.LoginRequest;
import com.legacy_lens.dto.request.RegisterRequest;
import com.legacy_lens.dto.response.AuthResponse;
import com.legacy_lens.entity.User;
import com.legacy_lens.enums.AccountStatus;
import com.legacy_lens.enums.Role;
import com.legacy_lens.exception.EmailAlreadyExistsException;
import com.legacy_lens.exception.OtpException;
import com.legacy_lens.repository.UserRepository;
import com.legacy_lens.security.JwtService;
import com.legacy_lens.service.AuthService;
import com.legacy_lens.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final OtpService otpService;

    // ─── Register ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(AccountStatus.ACTIVE);
        // isVerified stays false — entity default

        userRepository.save(user);

        // Generate OTP, persist, and send email (async)
        otpService.generateAndSendOtp(user);

        return user.getEmail();
    }

    // ─── Login ─────────────────────────────────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {
        // BCrypt password check via DaoAuthenticationProvider
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Block unverified users from obtaining a token
        if (!user.isVerified()) {
            throw new OtpException(
                "Please verify your email before logging in. Check your inbox for the OTP."
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .publicId(user.getPublicId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
