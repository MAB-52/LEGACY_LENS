package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "explanations")
@Getter
@Setter
public class Explanation extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
}