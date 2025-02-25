package com.forum.project.domain.comment.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.aspect.ExtractUserId;
import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.comment.vo.CommentContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @RequestMapping(value = "/{questionId}", method = RequestMethod.POST)
    public ResponseEntity<CommentResponseDto> addComment(
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        CommentCreateDto dto = CommentCreateDto.fromCommentRequestDto(commentRequestDto)
                .questionId(questionId)
                .userId(userId).build();
        CommentResponseDto commentResponseDto = commentService.addComment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponseDto);
    }

    @RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
    public ResponseEntity<List<CommentResponseDto>> getCommentsByQuestionId(
            @PathVariable Long questionId
    ) {
        List<CommentResponseDto> comments = commentService.getCommentsByQuestionId(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<List<CommentResponseDto>> getCommentsByUserComments(
            @ExtractUserId Long userId
    ) {
        List<CommentResponseDto> comments = commentService.getUserComments(userId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @RequestMapping(value = "/child/{parentCommentId}", method = RequestMethod.GET)
    public ResponseEntity<List<CommentResponseDto>> getChildComments(
            @PathVariable Long parentCommentId
    ) {
        List<CommentResponseDto> comments = commentService.getChildComments(parentCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @RequestMapping(value = "/{questionId}/{commentId}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponseDto> updateComment(
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        commentService.updateComment(commentRequestDto, new CommentContext(userId, questionId, commentId));
        return BaseResponseDto.buildOkResponse("Comment updated successfully.");
    }

    @RequestMapping(value = "/{questionId}/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponseDto> deleteComment(
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(new CommentContext(userId, questionId, commentId));
        return BaseResponseDto.buildOkResponse("Comment deleted successfully.");
    }
}
