package com.forum.project.presentation.comment;

import com.forum.project.domain.Comment;
import lombok.Getter;

@Getter
public class RequestCommentDto {
    private String content;
    private String userId;

    public static Comment toEntity(RequestCommentDto requestCommentDto) {
        return new Comment(requestCommentDto.getContent(), requestCommentDto.getUserId());
    }
}
