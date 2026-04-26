package com.legacy_lens.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    @Enumerated(EnumType.STRING)
    private Sender sender;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column(columnDefinition = "JSON")
    private String metadata;
}
