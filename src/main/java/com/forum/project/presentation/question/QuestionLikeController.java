package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionLikeService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.common.utils.IpAddressUtil;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.presentation.dtos.BaseResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions/like")
@RequiredArgsConstructor
public class QuestionLikeController {
    private final AuthenticationService authenticationService;
    private final QuestionLikeService questionLikeService;

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponseDto> addQuestionLike(
            @PathVariable(value = "id") Long questionId,
            @RequestParam String status,
            @RequestHeader("Authorization") String header,
            HttpServletRequest request
    ) {
        String ipAddress = IpAddressUtil.getClientIp(request);
        Long userId = authenticationService.extractUserId(header);
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
        Long userId = authenticationService.extractUserId(header);
        LikeStatus likeStatus = LikeStatus.fromString(status);
        questionLikeService.cancelLike(questionId, userId, likeStatus);
        BaseResponseDto response = new BaseResponseDto(String.format("Delete %s Question successfully.", status));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
