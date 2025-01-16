package com.forum.project.domain.question;

import com.forum.project.domain.comment.CommentReport;

import java.util.List;
import java.util.Optional;

public interface QuestionReportRepository {
    boolean existsByIdAndUserId(Long questionId, Long userId);

    void save(QuestionReport report);

    Optional<QuestionReport> findById(Long reportId);

    List<QuestionReport> findAllByUserId(Long userId);

    List<QuestionReport> findAllByQuestionId(Long questionId);

    Long countByQuestionId(Long questionId);
}
