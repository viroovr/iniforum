package com.forum.project.application.converter;

import com.forum.project.domain.entity.Comment;
import com.forum.project.domain.entity.Question;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoConverter {

    public Comment fromRequestDtoToEntity(RequestCommentDto requestCommentDto) {
        return Comment.builder()
                .content(requestCommentDto.getContent())
                .build();
    }

    public RequestCommentDto toRequestCommentDto(Comment question) {
        return new RequestCommentDto(
                question.getContent()
        );
    }

    public ResponseCommentDto toResponseCommentDto(Comment comment) {
        return new ResponseCommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getUserId(),
                comment.getCreatedDate(),
                comment.getLikeCount()
        );
    }
}
