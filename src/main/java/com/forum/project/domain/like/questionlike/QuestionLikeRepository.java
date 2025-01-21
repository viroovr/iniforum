package com.forum.project.domain.like.questionlike;

import java.util.Optional;

public interface QuestionLikeRepository {
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);

    QuestionLike save(QuestionLike build);

    Optional<QuestionLike> findByQuestionIdAndUserID(Long questionId, Long userId);

    void delete(Long questionId, Long userId);
}
