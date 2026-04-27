package com.legacy_lens.service;

import com.legacy_lens.entity.User;

public interface OtpService {

    /**
     * Generates a 6-digit OTP, persists it, and emails it to the user.
     */
    void generateAndSendOtp(User user);

    /**
     * Validates the OTP for the given email.
     * Marks it used and sets user.isVerified = true on success.
     * Throws {@link com.legacy_lens.exception.OtpException} on failure.
     */
    void verifyOtp(String email, String otpCode);
}
