package com.forum.project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long commentId;
    private String userId;

    public CommentLike() {}

    public CommentLike(Long commentId, String userId) {
        this.commentId = commentId;
        this.userId = userId;
    }


}
