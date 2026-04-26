package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "uploads")
@Getter
@Setter
public class Upload extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private String githubUrl;

    private Integer totalFiles;

    @Enumerated(EnumType.STRING)
    private UploadStatus status;

    @OneToMany(mappedBy = "upload", fetch = FetchType.LAZY)
    private List<FileEntity> files;
}