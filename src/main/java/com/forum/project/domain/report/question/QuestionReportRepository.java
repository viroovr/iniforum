package com.forum.project.domain.report.question;

import java.util.List;
import java.util.Optional;

public interface QuestionReportRepository {
    boolean existsByIdAndUserId(Long questionId, Long userId);

    QuestionReport save(QuestionReport report);

    Optional<QuestionReport> findById(Long reportId);

    List<QuestionReport> findAllByUserId(Long userId);

    List<QuestionReport> findAllByQuestionId(Long questionId);

    Long countByQuestionId(Long questionId);
}
