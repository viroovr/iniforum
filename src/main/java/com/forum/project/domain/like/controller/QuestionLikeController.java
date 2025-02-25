package com.forum.project.domain.like.controller;

import com.forum.project.domain.auth.aspect.ExtractIp;
import com.forum.project.domain.auth.aspect.ExtractUserId;
import com.forum.project.domain.like.service.QuestionLikeService;
import com.forum.project.core.common.IpAddressUtil;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.core.base.BaseResponseDto;
import com.forum.project.domain.question.vo.QuestionContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionLikeController {
    private final QuestionLikeService questionLikeService;

    @RequestMapping(value = "/like/{questionId}", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponseDto> likeQuestion(
            @ExtractIp String ip,
            @ExtractUserId Long userId,
            @PathVariable Long questionId
    ) {
        questionLikeService.likeQuestion(new QuestionContext(questionId, userId), ip);
        return BaseResponseDto.buildOkResponse("Like Question successfully.");
    }

    @DeleteMapping("/like/{questionId}")
    public ResponseEntity<BaseResponseDto> deleteQuestionLike(
            @ExtractUserId Long userId,
            @PathVariable Long questionId
    ) {
        questionLikeService.cancelLike(new QuestionContext(questionId, userId));
        return BaseResponseDto.buildOkResponse("Delete Question like successfully.");
    }
}
