package com.forum.project.presentation.controller;

import com.forum.project.application.question.CommentService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final TokenService tokenService;

    @PostMapping(value = "/{questionId}")
    public ResponseEntity<ResponseCommentDto> addComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long questionId,
            @RequestBody RequestCommentDto requestCommentDto
    ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        ResponseCommentDto responseCommentDto = commentService.addComment(questionId, requestCommentDto, accessToken);
        return ResponseEntity.status(HttpStatus.OK).body(responseCommentDto);
    }

    @GetMapping(value = "/{questionId}")
    public ResponseEntity<List<ResponseCommentDto>> getComments(
            @PathVariable Long questionId
    ) {
        List<ResponseCommentDto> comments = commentService.getCommentsByQuestionId(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseCommentDto> updateComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable("id") Long commentId,
            @RequestBody RequestCommentDto requestCommentDto
    ) {
        String jwt = tokenService.extractTokenByHeader(header);
        ResponseCommentDto responseCommentDto = commentService.updateComment(commentId, requestCommentDto, jwt);
        return ResponseEntity.status(HttpStatus.OK).body(responseCommentDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        String jwt = tokenService.extractTokenByHeader(header);
        commentService.deleteComment(id, jwt);
        Map<String, String> response = Map.of("message", "Comment deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> likeComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        String jwt = tokenService.extractTokenByHeader(header);
        commentService.likeComment(id, jwt);
        Map<String, String> response = Map.of("message", "Comment liked successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
