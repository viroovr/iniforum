package com.forum.project.application.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.like.LikeStatus;
import com.forum.project.domain.like.questionlike.QuestionLike;
import com.forum.project.domain.like.questionlike.QuestionLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionLikeService {
    private final QuestionLikeRepository questionLikeRepository;

    @Transactional
    public void addLike(Long questionId, Long userId) {
        if(questionLikeRepository.existsByQuestionIdAndUserId(questionId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        questionLikeRepository.save(QuestionLike.builder()
                .questionId(questionId)
                .status(LikeStatus.LIKE.name())
                .userId(userId).build());
    }

    @Transactional
    public void addDislikeComment(Long questionId, Long userId) {
        if(questionLikeRepository.existsByQuestionIdAndUserId(questionId, userId))
            throw new ApplicationException(ErrorCode.LIKE_ALREADY_EXISTS);

        questionLikeRepository.save(QuestionLike.builder()
                .userId(userId)
                .status(LikeStatus.DISLIKE.name())
                .questionId(questionId).build());
    }
}
