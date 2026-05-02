package com.legacy_lens.service;

import com.legacy_lens.dto.request.LoginRequest;
import com.legacy_lens.dto.request.RegisterRequest;
import com.legacy_lens.dto.response.AuthResponse;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
