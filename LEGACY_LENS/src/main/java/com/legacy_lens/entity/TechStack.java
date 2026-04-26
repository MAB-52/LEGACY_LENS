package com.legacy_lens.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tech_stacks")
@Getter
@Setter
public class TechStack extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    private String techName;
}
