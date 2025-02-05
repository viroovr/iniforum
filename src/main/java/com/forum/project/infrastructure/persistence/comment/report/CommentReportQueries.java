package com.forum.project.infrastructure.persistence.comment.report;

public class CommentReportQueries {
    public static String insert() {
        return "INSERT INTO comment_reports " +
                "(user_id, comment_id, reason, status) " +
                "VALUES " +
                "(:userId, :commentId, :reason, :status)";
    }

    public static String existsByCommentIdAndUserId() {
        return "SELECT EXISTS (SELECT 1 FROM comment_reports " +
                "WHERE comment_id=:commentId AND user_id=:userId)";
    }

    public static String countByCommentId() {
        return "SELECT COUNT(*) FROM comment_reports " +
                "WHERE comment_id=:commentId";
    }

    public static String findAllByCommentId() {
        return "SELECT * FROM comment_reports " +
                "WHERE comment_id=:commentId";
    }

    public static String findAllByUserId() {
        return "SELECT * FROM comment_reports " +
                "WHERE user_id=:userId";
    }

    public static String findById() {
        return "SELECT * FROM comment_reports WHERE id=:id";
    }

    public static String delete() {
        return "DELETE FROM comment_reports WHERE id=:id";
    }


}
