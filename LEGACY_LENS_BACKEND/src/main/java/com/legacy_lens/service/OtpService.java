package com.legacy_lens.service;

import com.legacy_lens.entity.User;

public interface OtpService {

    void generateAndSendOtp(User user);

    void verifyOtp(String email, String otpCode);
}
