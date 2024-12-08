package com.forum.project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "questions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String userId;

    private String content;

    private String tag;

    private LocalDateTime createdDate;

    private Integer viewCount;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Question(Long id, String title, String userId, String content, String tag) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
    }

    public Question(Long id, String title, String userId, String content, String tag, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
        this.createdDate = createdDate;
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
