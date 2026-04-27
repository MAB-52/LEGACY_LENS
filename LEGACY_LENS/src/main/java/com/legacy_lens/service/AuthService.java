package com.legacy_lens.service;

import com.legacy_lens.dto.request.LoginRequest;
import com.legacy_lens.dto.request.RegisterRequest;
import com.legacy_lens.dto.response.AuthResponse;

public interface AuthService {

    /**
     * Registers a new user and sends OTP email.
     * Returns the user's email. JWT is NOT issued until email is verified.
     */
    String register(RegisterRequest request);

    /**
     * Validates credentials and issues JWT.
     * Throws OtpException if email is not yet verified.
     */
    AuthResponse login(LoginRequest request);
}
