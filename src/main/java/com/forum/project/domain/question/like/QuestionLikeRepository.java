package com.forum.project.domain.question.like;

import java.util.Optional;

public interface QuestionLikeRepository {
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);

    QuestionLike insert(QuestionLike build);

    Optional<QuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId);

    void delete(Long questionId, Long userId);
}
