package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
@Getter
@Setter
public class UserToken extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private Integer totalTokens;
    private Integer usedTokens;
    private Integer remainingTokens;

    @Enumerated(EnumType.STRING)
    private ResetInterval resetInterval;

    private LocalDateTime lastResetTime;
    private LocalDateTime nextResetTime;
}