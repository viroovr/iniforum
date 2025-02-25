package com.forum.project.domain.report.repository;

import com.forum.project.domain.report.dto.CommentReportCreateDto;
import com.forum.project.domain.report.entity.CommentReport;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentReportRepository {
    Map<String, Object> insertAndReturnGeneratedKeys(CommentReportCreateDto commentReportCreateDto);

    List<CommentReport> findAllByCommentId(Long commentId);
    List<CommentReport> findAllByUserId(Long userId);
    Optional<CommentReport> findById(Long reportId);
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Long countByCommentId(Long commentId);

    void delete(Long id);
}
