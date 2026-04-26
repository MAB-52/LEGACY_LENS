package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "flow_diagrams")
@Getter
@Setter
public class FlowDiagram extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Analysis analysis;

    @Column(columnDefinition = "LONGTEXT")
    private String diagramJson;
}
