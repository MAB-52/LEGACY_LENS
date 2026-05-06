package com.legacy_lens.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legacy_lens.enums.AccountStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {

    private String publicId;
    private String name;
    private String email;
    private AccountStatus status;

    @JsonProperty("isVerified")
    private boolean isVerified;

    private LocalDateTime createdAt;
}