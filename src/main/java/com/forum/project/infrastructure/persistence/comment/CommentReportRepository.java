package com.forum.project.infrastructure.persistence.comment;

import com.forum.project.domain.comment.CommentReport;

public interface CommentReportRepository {
    CommentReport save(CommentReport commentReport);
}
