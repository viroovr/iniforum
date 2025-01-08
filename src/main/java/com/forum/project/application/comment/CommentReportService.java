package com.forum.project.application.comment;

import com.forum.project.domain.comment.CommentReport;
import com.forum.project.infrastructure.persistence.comment.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentReportRepository commentReportRepository;

    public void saveReportComment(Long commentId, Long userId, String reason) {
        CommentReport report = new CommentReport();
        report.initialize(commentId, userId, reason);
        commentReportRepository.save(report);
    }
}
