package com.legacy_lens.service;

public interface EmailService {

    /**
     * Sends the OTP verification email to the newly registered user.
     *
     * @param toEmail  recipient email address
     * @param name     recipient's display name (used in the email greeting)
     * @param otpCode  the 6-digit OTP to embed in the email
     */
    void sendOtpEmail(String toEmail, String name, String otpCode);
}
