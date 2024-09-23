package com.forum.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String userId;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @Setter
    private Question question;

    public Comment() {}
    public Comment(Long id, String content, String userId, Question question) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.question = question;
    }
    public Comment(String content, String userId) {
        this.content = content;
        this.userId = userId;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

}
