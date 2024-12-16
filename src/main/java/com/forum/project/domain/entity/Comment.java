package com.forum.project.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String userId;

    private LocalDateTime createdDate;
    //    @ManyToOne
    //    @JoinColumn(name = "question_id", nullable = false)
    private Long questionId;

    private Long likeCount;
//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentLike> likes = new ArrayList<>();

    public Comment(String content, String userId) {
        this.content = content;
        this.userId = userId;
    }
}
