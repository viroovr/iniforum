package com.forum.project.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private LocalDateTime createdDate;

    @Column(nullable = true)
    private String profileImagePath;

    public User(String password, String profileImagePath, String nickname) {
        this.password = password;
        this.profileImagePath = profileImagePath;
        this.nickname = nickname;
    }

    @Column(nullable = false, unique = true)
    private String nickname;

    public User(String userId, String email, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
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
