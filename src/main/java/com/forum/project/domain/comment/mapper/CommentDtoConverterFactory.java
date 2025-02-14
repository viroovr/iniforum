package com.forum.project.domain.comment.mapper;

import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
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
                .loginId(comment.getLoginId())
                .createdDate(comment.getCreatedDate())
                .likeCount(comment.getUpVotedCount())
                .build();
    }
}
