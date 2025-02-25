package com.forum.project.domain.like.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.aspect.ExtractUserId;
import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.comment.vo.CommentContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/comments")
public class CommentLikeController {
    private final CommentService commentService;

    @RequestMapping(value = "/like/{questionId}/{commentId}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponseDto> likeComment(
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @PathVariable Long commentId
    ) {
        commentService.likeComment(new CommentContext(userId, questionId, commentId));
        return BaseResponseDto.buildOkResponse("Comment liked successfully.");
    }

    @RequestMapping(value = "/dislike/{questionId}/{commentId}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponseDto> dislikeComment(
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @PathVariable Long commentId
    ) {
        commentService.dislikeComment(new CommentContext(userId, questionId, commentId));
        return BaseResponseDto.buildOkResponse("Comment disliked successfully.");
    }
}
