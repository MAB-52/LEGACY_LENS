package com.legacy_lens.entity;

import com.legacy_lens.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "token_usage")
@Getter
@Setter
public class TokenUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    private Integer tokensUsed;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;
}