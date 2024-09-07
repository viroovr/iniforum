package com.forum.project.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String userId;
    private String content;
    private String tag;
    private LocalDateTime createdDate;

    public Question(Long id, String title, String userId, String content, String tag) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
    }

    public Question(String title, String userId, String content, String tag) {
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}
