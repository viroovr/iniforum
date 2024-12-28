package com.forum.project.presentation.comment;

import com.forum.project.application.question.CommentService;
import com.forum.project.application.jwt.TokenService;
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
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long questionId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        String accessToken = tokenService.extractTokenByHeader(header);
        CommentResponseDto commentResponseDto = commentService.addComment(questionId, commentRequestDto, accessToken);
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
    public ResponseEntity<CommentResponseDto> updateComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable("id") Long commentId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        String jwt = tokenService.extractTokenByHeader(header);
        CommentResponseDto commentResponseDto = commentService.updateComment(commentId, commentRequestDto, jwt);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);
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
