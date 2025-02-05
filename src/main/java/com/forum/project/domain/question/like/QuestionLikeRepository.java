package com.forum.project.domain.question.like;

import java.util.Map;
import java.util.Optional;

public interface QuestionLikeRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(QuestionLike build);

    Optional<QuestionLike> findById(Long id);
    Optional<QuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId);
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);

    void deleteByQuestionIdAndUserId(Long questionId, Long userId);
}
