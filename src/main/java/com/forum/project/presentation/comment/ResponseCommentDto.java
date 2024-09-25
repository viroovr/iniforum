package com.forum.project.presentation.comment;

import com.forum.project.domain.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ResponseCommentDto {
    private Long id;

    private String content;

    private String userId;

    private LocalDateTime createdDate;

    private Long likeCount;

    public ResponseCommentDto(Long id, String content, String userId, LocalDateTime localDateTime, Long likeCount) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createdDate = localDateTime;
        this.likeCount = likeCount;
    }

    public static ResponseCommentDto toDto(Comment comment) {
        return new ResponseCommentDto(comment.getId(), comment.getContent(), comment.getUserId(), comment.getCreatedDate(),
            comment.getLikeCount());
    }
}
