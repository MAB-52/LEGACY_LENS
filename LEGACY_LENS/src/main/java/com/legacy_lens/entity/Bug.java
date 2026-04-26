package com.legacy_lens.entity;

import com.legacy_lens.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bugs")
@Getter
@Setter
public class Bug extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    private String title;
    private String fileLocation;

    @Column(columnDefinition = "TEXT")
    private String description;
}
