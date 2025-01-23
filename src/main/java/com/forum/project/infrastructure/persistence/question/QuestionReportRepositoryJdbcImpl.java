package com.forum.project.infrastructure.persistence.question;

import com.forum.project.domain.question.report.QuestionReport;
import com.forum.project.domain.question.report.QuestionReportRepository;

import java.util.List;
import java.util.Optional;

public class QuestionReportRepositoryJdbcImpl implements QuestionReportRepository {
    @Override
    public boolean existsByIdAndUserId(Long questionId, Long userId) {
        return false;
    }

    @Override
    public QuestionReport save(QuestionReport report) {
        return new QuestionReport();
    }

    @Override
    public Optional<QuestionReport> findById(Long reportId) {
        return Optional.empty();
    }

    @Override
    public List<QuestionReport> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<QuestionReport> findAllByQuestionId(Long questionId) {
        return List.of();
    }

    @Override
    public Long countByQuestionId(Long questionId) {
        return 0L;
    }
}
