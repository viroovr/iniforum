package com.forum.project.domain.comment.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentReportRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(CommentReport commentReport);

    List<CommentReport> findAllByCommentId(Long commentId);
    List<CommentReport> findAllByUserId(Long userId);
    Optional<CommentReport> findById(Long reportId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Long countByCommentId(Long commentId);

    void delete(Long id);
}
