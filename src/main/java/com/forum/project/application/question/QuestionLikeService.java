package com.forum.project.application.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.question.like.QuestionLike;
import com.forum.project.domain.question.like.QuestionLikeRepository;
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

        questionLikeRepository.insert(QuestionLike.builder()
                .questionId(questionId)
                .status(likeStatus.name())
                .userId(userId)
                .ipAddress(ipAddress)
                .build());

        questionCrudService.updateUpVotedCount(questionId, likeStatus);
    }

    public void cancelLike(Long questionId, Long userId, LikeStatus likeStatus) {
        if(!questionLikeRepository.existsByQuestionIdAndUserId(questionId, userId))
            throw new ApplicationException(ErrorCode.LIKE_NOT_FOUND);

        questionLikeRepository.delete(questionId, userId);

        questionCrudService.decrementVotedCount(questionId, likeStatus);
    }
}
