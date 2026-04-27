package com.legacy_lens.service.impl;

import com.legacy_lens.entity.OtpVerification;
import com.legacy_lens.entity.User;
import com.legacy_lens.exception.OtpException;
import com.legacy_lens.repository.OtpVerificationRepository;
import com.legacy_lens.repository.UserRepository;
import com.legacy_lens.service.EmailService;
import com.legacy_lens.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Generate & Send

    @Override
    @Transactional
    public void generateAndSendOtp(User user) {
        // Delete any previous unused OTPs for this user (clean slate)
        otpRepository.deleteAllByUser(user);

        String code = generateSixDigitOtp();

        OtpVerification otp = new OtpVerification();
        otp.setUser(user);
        otp.setOtpCode(code);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otp.setUsed(false);

        otpRepository.save(otp);

        // Async email — returns immediately
        emailService.sendOtpEmail(user.getEmail(), user.getName(), code);
    }

    // Verify

    @Override
    @Transactional
    public void verifyOtp(String email, String otpCode) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OtpException("No account found for this email"));

        if (user.isVerified()) {
            throw new OtpException("Email is already verified");
        }

        OtpVerification otp = otpRepository
                .findTopByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new OtpException("No active OTP found. Please request a new one."));

        if (otp.isExpired()) {
            throw new OtpException("OTP has expired. Please request a new one.");
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            throw new OtpException("Invalid OTP. Please check and try again.");
        }

        // Mark OTP as consumed
        otp.setUsed(true);
        otpRepository.save(otp);

        // Mark user as verified
        user.setVerified(true);
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", email);
    }

    // Helper

    private String generateSixDigitOtp() {
        // Produces a zero-padded 6-digit string e.g. "047382"
        int code = SECURE_RANDOM.nextInt(900_000) + 100_000;  // 100000–999999
        return String.valueOf(code);
    }
}
