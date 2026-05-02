package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Chat> chats;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Upload> uploads;
}
