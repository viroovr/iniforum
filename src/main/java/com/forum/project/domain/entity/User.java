package com.forum.project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name",nullable = false)
    private String name;

    private LocalDateTime createdDate;

    @Column(name = "profile_image_path", nullable = true)
    private String profileImagePath;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    public User(String password, String profileImagePath, String nickname) {
        this.password = password;
        this.profileImagePath = profileImagePath;
        this.nickname = nickname;
    }

    public User(String userId, String email, String password, String name, LocalDateTime createdDate, String profileImagePath, String nickname) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdDate = createdDate;
        this.profileImagePath = profileImagePath;
        this.nickname = nickname;
    }
}
