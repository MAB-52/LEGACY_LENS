package com.legacy_lens.controller;

import com.legacy_lens.common.ApiResponse;
import com.legacy_lens.dto.request.LoginRequest;
import com.legacy_lens.dto.request.RegisterRequest;
import com.legacy_lens.dto.request.ResendOtpRequest;
import com.legacy_lens.dto.request.VerifyOtpRequest;
import com.legacy_lens.dto.response.AuthResponse;
import com.legacy_lens.service.AuthService;
import com.legacy_lens.service.OtpService;
import com.legacy_lens.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        String email = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Registration successful. Please check your email for the OTP to verify your account.",
                        email
                ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(
                ApiResponse.success("Email verified successfully. You can now log in.", null)
        );
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(otpService::generateAndSendOtp);
        // Always return 200 — don't leak whether the email exists
        return ResponseEntity.ok(
                ApiResponse.success("If that email is registered, a new OTP has been sent.", null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
