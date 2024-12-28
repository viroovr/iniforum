package com.forum.project.application.converter;

import com.forum.project.domain.comment.Comment;
import com.forum.project.presentation.comment.CommentRequestDto;
import com.forum.project.presentation.comment.CommentResponseDto;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoConverterFactory {

    public static Comment fromRequestDtoToEntity(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .content(commentRequestDto.getContent())
                .build();
    }

    public static CommentResponseDto toResponseCommentDto(Comment comment) {
        return CommentResponseDto.builder()
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .likeCount(comment.getUpVotedCount())
                .build();
    }
}
