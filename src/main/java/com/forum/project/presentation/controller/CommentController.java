package com.forum.project.presentation.controller;

import com.forum.project.application.CommentService;
import com.forum.project.presentation.dtos.comment.RequestCommentDto;
import com.forum.project.presentation.dtos.comment.ResponseCommentDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/q")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @RequestMapping(value = "/{questionId}/comments", method= RequestMethod.POST)
    public ResponseEntity<ResponseCommentDto> addComment(
            @PathVariable Long questionId,
            @RequestBody RequestCommentDto requestCommentDto
    ) {
        ResponseCommentDto responseCommentDto = commentService.addComment(questionId, requestCommentDto);
        return ResponseEntity.ok(responseCommentDto);
    }

    @RequestMapping(value = "/{questionId}/comments", method = RequestMethod.GET)
    public ResponseEntity<List<ResponseCommentDto>> getComments(
            @PathVariable Long questionId
    ) {
        List<ResponseCommentDto> comments = commentService.getCommentsByQuestionId(questionId);
        return ResponseEntity.ok(comments);
    }

    @RequestMapping(value = "/{questionId}/comments/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseCommentDto> updateComment(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable Long id,
            @RequestBody RequestCommentDto requestCommentDto
    ) {
        try {
            String jwt = extractToken(token);
            ResponseCommentDto responseCommentDto = commentService.updateComment(id, requestCommentDto, jwt);
            return ResponseEntity.ok(responseCommentDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @RequestMapping(value = "/{questionId}/comments/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteComment(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String token
    ) {
        try {
            String jwt = extractToken(token);
            commentService.deleteComment(id, jwt);
            return ResponseEntity.ok("Comment deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found.");
        }
    }

    private String extractToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    @RequestMapping(value = "/comments/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<?> likeComment(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization") String token
    ) {
        try {
            String jwt = extractToken(token);
            commentService.likeComment(id, jwt);
            return ResponseEntity.ok("Comment liked successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
