package com.forum.project.domain.question.controller;

import com.forum.project.domain.like.service.QuestionLikeService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.core.common.IpAddressUtil;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.core.base.BaseResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions/like")
@RequiredArgsConstructor
public class QuestionLikeController {
    private final AuthorizationService authorizationService;
    private final QuestionLikeService questionLikeService;

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDto> addQuestionLike(
            @PathVariable(value = "id") Long questionId,
            @RequestParam String status,
            @RequestHeader("Authorization") String header,
            HttpServletRequest request
    ) {
        String ipAddress = IpAddressUtil.getClientIp(request);
        Long userId = authorizationService.extractUserId(header);
        LikeStatus likeStatus = LikeStatus.fromString(status);

        questionLikeService.addLike(questionId, userId, likeStatus, ipAddress);
        BaseResponseDto response = new BaseResponseDto(String.format("%s Question successfully.", status));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDto> deleteQuestionLike(
            @PathVariable(value = "id") Long questionId,
            @RequestParam String status,
            @RequestHeader("Authorization") String header
    ) {
        Long userId = authorizationService.extractUserId(header);
        LikeStatus likeStatus = LikeStatus.fromString(status);
        questionLikeService.cancelLike(questionId, userId, likeStatus);
        BaseResponseDto response = new BaseResponseDto(String.format("Delete %s Question successfully.", status));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
