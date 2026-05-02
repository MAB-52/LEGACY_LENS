package com.legacy_lens.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
public class FileEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Upload upload;

    private String fileName;
    private String fileType;
    private Long fileSize;

    private String filePath;
}