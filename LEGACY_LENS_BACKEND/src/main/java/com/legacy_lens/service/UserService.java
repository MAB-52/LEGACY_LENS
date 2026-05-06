package com.legacy_lens.service;

import com.legacy_lens.dto.response.PagedResponseDto;
import com.legacy_lens.dto.response.UserResponseDto;

public interface UserService {

    PagedResponseDto<UserResponseDto> getAllUsers(int page, int size, String search);
    byte[] exportUsersCsv(String search);
}
