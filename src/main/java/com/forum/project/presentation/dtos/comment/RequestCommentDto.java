package com.forum.project.presentation.dtos.comment;

import com.forum.project.domain.entity.Comment;
import lombok.Getter;

@Getter
public class RequestCommentDto {
    private String content;
    private String userId;

    public static Comment toEntity(RequestCommentDto requestCommentDto) {
        return new Comment(requestCommentDto.getContent(), requestCommentDto.getUserId());
    }
}
