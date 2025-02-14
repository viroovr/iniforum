package com.forum.project.domain.like.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.like.vo.LikeStatus;
import com.forum.project.domain.like.entity.QuestionLike;
import com.forum.project.domain.like.repository.QuestionLikeRepository;
import com.forum.project.domain.question.service.QuestionCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionLikeService {
    private final QuestionLikeRepository questionLikeRepository;
    private final QuestionCrudService questionCrudService;

    @Transactional
    public void addLike(Long questionId, Long userId, LikeStatus likeStatus, String ipAddress) {
        if(questionLikeRepository.existsByQuestionIdAndUserId(questionId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        questionLikeRepository.insertAndReturnGeneratedKeys(QuestionLike.builder()
                .questionId(questionId)
                .status(likeStatus.name())
                .userId(userId)
                .ipAddress(ipAddress)
                .build());

        questionCrudService.incrementVotedCount(questionId, likeStatus);
    }

    public void cancelLike(Long questionId, Long userId, LikeStatus likeStatus) {
        if(!questionLikeRepository.existsByQuestionIdAndUserId(questionId, userId))
            throw new ApplicationException(ErrorCode.LIKE_NOT_FOUND);

        questionLikeRepository.deleteByQuestionIdAndUserId(questionId, userId);

        questionCrudService.decrementVotedCount(questionId, likeStatus);
    }
}
