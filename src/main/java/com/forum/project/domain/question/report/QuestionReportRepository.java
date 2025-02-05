package com.forum.project.domain.question.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionReportRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(QuestionReport report);

    Optional<QuestionReport> findById(Long id);
    List<QuestionReport> findAllByUserId(Long userId);
    List<QuestionReport> findAllByQuestionId(Long questionId);
    boolean existsByQuestionIdAndUserId(Long questionId, Long userId);

    Long countByQuestionId(Long questionId);

    void delete(Long id);
}
