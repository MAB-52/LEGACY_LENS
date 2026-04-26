package com.legacy_lens.entity;

import com.legacy_lens.enums.AnalysisStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "analyses")
@Getter
@Setter
public class Analysis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Upload upload;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;
}
