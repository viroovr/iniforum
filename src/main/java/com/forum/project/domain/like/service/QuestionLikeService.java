package com.forum.project.domain.like.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.domain.like.entity.QuestionLike;
import com.forum.project.domain.like.repository.QuestionLikeRepository;
import com.forum.project.domain.question.service.QuestionCrudService;
import com.forum.project.domain.question.vo.QuestionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionLikeService {
    private final QuestionCrudService questionCrudService;
    private final QuestionLikeRepository questionLikeRepository;

    private void validateDuplicateContext(QuestionContext context) {
        if(questionLikeRepository.existsByQuestionIdAndUserId(context.questionId(), context.userId()))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);
    }

    @Transactional
    public void likeQuestion(QuestionContext context, String ipAddress) {
        validateDuplicateContext(context);

        questionLikeRepository.insertAndReturnGeneratedKeys(QuestionLike.builder()
                .questionId(context.questionId())
                .status(LikeStatus.LIKE.name())
                .userId(context.userId())
                .ipAddress(ipAddress)
                .build());

        questionCrudService.incrementVotedCount(context.questionId(), LikeStatus.LIKE);
    }

    private void validateExistingContext(QuestionContext context) {
        if(questionLikeRepository.existsByQuestionIdAndUserId(context.questionId(), context.userId()))
            throw new ApplicationException(ErrorCode.LIKE_NOT_FOUND);
    }

    public void cancelLike(QuestionContext context) {
        validateExistingContext(context);

        questionLikeRepository.deleteByQuestionIdAndUserId(context.questionId(), context.userId());

        questionCrudService.decrementVotedCount(context.questionId(), LikeStatus.LIKE);
    }
}
