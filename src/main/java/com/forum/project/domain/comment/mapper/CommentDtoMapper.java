package com.forum.project.domain.comment.mapper;

import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper {

    public static Comment toEntity(CommentCreateDto dto, CommentKey commentKey) {
        Comment comment =  Comment.builder()
                .parentCommentId(dto.getParentCommentId())
                .status(dto.getStatus().name())
                .userId(dto.getUserId())
                .questionId(dto.getQuestionId())
                .content(dto.getContent())
                .build();
        comment.setKeys(commentKey);
        return comment;
    }

    public static Comment fromRequestDtoToEntity(CommentRequestDto commentRequestDto) {
        return Comment.builder()
                .content(commentRequestDto.getContent())
                .build();
    }

    public static CommentResponseDto toResponseCommentDto(Comment comment, String loginId) {
        return CommentResponseDto.builder()
                .content(comment.getContent())
                .loginId(loginId)
                .createdDate(comment.getCreatedDate())
                .likeCount(comment.getUpVotedCount())
                .build();
    }
}
