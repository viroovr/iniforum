package com.forum.project.domain.comment.controller;

import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.auth.service.AuthenticationService;
import com.forum.project.domain.comment.dto.CommentRequestDto;
import com.forum.project.domain.comment.dto.CommentResponseDto;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.core.base.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final AuthenticationService authenticationService;
    private final CommentService commentService;

    @PostMapping(value = "/{questionId}")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long questionId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        Long userId = authenticationService.extractUserId(header);
        CommentResponseDto commentResponseDto = commentService.addComment(questionId, userId, commentRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);
    }

    @GetMapping(value = "/{questionId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long questionId
    ) {
        List<CommentResponseDto> comments = commentService.getCommentsByQuestionId(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponseDto> updateComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable("id") Long commentId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        Long userId = authenticationService.extractUserId(header);
        commentService.updateComment(commentId, userId, commentRequestDto);
        BaseResponseDto response = new BaseResponseDto("Comment updated successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponseDto> deleteComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        commentService.deleteComment(id, header);
        BaseResponseDto response = new BaseResponseDto("Comment deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> likeComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        Long userId = authenticationService.extractUserId(header);
        commentService.likeComment(id, userId);
        BaseResponseDto response = new BaseResponseDto("Comment liked successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/dislike", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> dislikeComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        Long userId = authenticationService.extractUserId(header);
        commentService.dislikeComment(id, userId);
        BaseResponseDto response = new BaseResponseDto("Comment disliked successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/report", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> reportComment(
            @RequestBody ReportRequestDto dto,
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        Long userId = authenticationService.extractUserId(header);
        commentService.reportComment(id, userId, dto);
        BaseResponseDto response = new BaseResponseDto("Comment reported successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<CommentResponseDto>> reportComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long userId
    ) {
        List<CommentResponseDto> response = commentService.getUserComments(userId, header);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
