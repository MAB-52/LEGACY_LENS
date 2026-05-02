package com.legacy_lens.dto.response;

import com.legacy_lens.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String publicId;
    private String name;
    private String email;
    private Role role;
}
