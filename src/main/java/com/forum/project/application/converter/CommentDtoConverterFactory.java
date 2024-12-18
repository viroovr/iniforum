package com.forum.project.application.converter;

import com.forum.project.domain.entity.Comment;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoConverterFactory {

    public static Comment fromRequestDtoToEntity(RequestCommentDto requestCommentDto) {
        return Comment.builder()
                .content(requestCommentDto.getContent())
                .build();
    }

    public static ResponseCommentDto toResponseCommentDto(Comment comment) {
        return ResponseCommentDto.builder()
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .likeCount(comment.getUpVotedCount())
                .build();
    }
}
