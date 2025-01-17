package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.domain.report.comment.CommentReport;
import com.forum.project.domain.report.comment.CommentReportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentReportJdbcImpl implements CommentReportRepository {
    @Override
    public CommentReport save(CommentReport commentReport) {
        return null;
    }

    @Override
    public boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
        return false;
    }

    @Override
    public Long countByCommentId(Long commentId) {
        return 0L;
    }

    @Override
    public List<CommentReport> findAllByCommentId(Long commentId) {
        return List.of();
    }

    @Override
    public List<CommentReport> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Optional<CommentReport> findById(Long reportId) {
        return Optional.empty();
    }
}
