package com.forum.project.domain.like.questionlike;

public interface QuestionLikeRepository {
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);

    QuestionLike save(QuestionLike build);
}
