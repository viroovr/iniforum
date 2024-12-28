package com.forum.project.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private Long userId;
    private String loginId;
    private String content;
    private Long questionId;
    private Long upVotedCount;
    private Long downVotedCount;
    private CommentStatus status;
    private Long parentCommentId;
    private Long reportCount;
    private Boolean isEdited;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}