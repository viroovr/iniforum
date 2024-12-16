package com.forum.project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "comment_id", nullable = false)
    private Long commentId;

    public CommentLike(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}
