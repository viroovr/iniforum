package com.forum.project.presentation.comment;

import com.forum.project.application.comment.CommentService;
import com.forum.project.domain.report.ReportRequestDto;
import com.forum.project.presentation.dtos.BaseResponseDto;
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

    @PostMapping(value = "/{questionId}")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long questionId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        CommentResponseDto commentResponseDto = commentService.addComment(questionId, commentRequestDto, header);
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
        CommentResponseDto commentResponseDto = commentService.updateComment(commentId, commentRequestDto, header);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        commentService.deleteComment(id, header);
        Map<String, String> response = Map.of("message", "Comment deleted successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> likeComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        commentService.likeComment(id, header);
        BaseResponseDto response = new BaseResponseDto("Comment liked successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/dislike", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> dislikeComment(
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        commentService.dislikeComment(id, header);
        BaseResponseDto response = new BaseResponseDto("Comment disliked successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/{id}/report", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> reportComment(
            @RequestBody ReportRequestDto dto,
            @RequestHeader(value = "Authorization") String header,
            @PathVariable Long id
    ) {
        commentService.reportComment(id, header, dto);
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
