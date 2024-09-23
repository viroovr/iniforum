package com.forum.project.presentation.comment;

import com.forum.project.application.CommentService;
import com.forum.project.domain.Comment;
import lombok.RequiredArgsConstructor;
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

}
