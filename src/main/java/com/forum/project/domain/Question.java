package com.forum.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String userId;
    private String content;
    private String tag;
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Question() {}

    public Question(Long id, String title, String userId, String content, String tag) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
    }

    public Question(String title, String userId, String content, String tag, LocalDateTime localDateTime) {
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
        this.createdDate = localDateTime;
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
