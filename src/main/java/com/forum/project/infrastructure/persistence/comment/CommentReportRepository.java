package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.domain.comment.CommentReport;

import java.util.List;
import java.util.Optional;

public interface CommentReportRepository {
    CommentReport save(CommentReport commentReport);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Long countByCommentId(Long commentId);

    List<CommentReport> findAllByCommentId(Long commentId);

    List<CommentReport> findAllByUserId(Long userId);

    Optional<CommentReport> findById(Long reportId);
}
