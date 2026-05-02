package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "suggestions")
@Getter
@Setter
public class Suggestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}
