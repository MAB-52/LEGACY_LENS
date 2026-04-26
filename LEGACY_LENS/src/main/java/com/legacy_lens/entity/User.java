package com.legacy_lens.entity;

import com.legacy_lens.enums.AccountStatus;
import com.legacy_lens.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Project> projects;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Chat> chats;
}