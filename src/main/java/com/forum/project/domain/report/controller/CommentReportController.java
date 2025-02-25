package com.forum.project.domain.report.controller;

import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.auth.aspect.ExtractUserId;
import com.forum.project.domain.comment.service.CommentService;
import com.forum.project.domain.comment.vo.CommentContext;
import com.forum.project.domain.report.dto.ReportRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/comments/report")
public class CommentReportController {
    private final CommentService commentService;

    @RequestMapping(value = "/{questionId}/{commentId}", method = RequestMethod.POST)
    public ResponseEntity<BaseResponseDto> reportComment(
            @RequestBody ReportRequestDto dto,
            @ExtractUserId Long userId,
            @PathVariable Long questionId,
            @PathVariable Long commentId
    ) {
        commentService.reportComment(dto, new CommentContext(userId, questionId, commentId));
        return BaseResponseDto.buildOkResponse("Comment reported successfully.");
    }
}
