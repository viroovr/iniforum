package com.forum.project.infrastructure.persistence.queries;

public class CommentQueries {
    public static String findAllByQuestionId() {
        return "SELECT * FROM comments " +
                "WHERE question_id=:questionId";
    }

    public static String findAllByParentCommentId() {
        return "SELECT * FROM comments " +
                "WHERE parent_comment_id=:parentCommentId";
    }

    public static String findAllByUserId() {
        return "SELECT * FROM comments " +
                "WHERE user_id=:userId";
    }

    public static String findById() {
        return "SELECT * FROM comments " +
                "WHERE id=:id";
    }

    public static String insert() {
        return "INSERT INTO comments " +
                "(user_id, question_id, parent_comment_id, content, status) " +
                "VALUES " +
                "(:userId, :questionId, :parentCommentId, :content, :status)";
    }

    public static String updateContent() {
        return "UPDATE comments " +
                "SET " +
                    "content=:content, " +
                    "is_edited= TRUE, " +
                    "last_modified_date = CURRENT_TIMESTAMP " +
                "WHERE id=:id";
    }

    public static String updateDownVotedCount() {
        return "UPDATE comments " +
                "SET " +
                "down_voted_count= down_voted_count + :delta " +
                "WHERE id=:id";
    }

    public static String updateUpVotedCount() {
        return "UPDATE comments " +
                "SET " +
                "up_voted_count = up_voted_count + :delta " +
                "WHERE id=:id";
    }

    public static String deleteById() {
        return "DELETE FROM comments WHERE id=:id";
    }

    public static String existsById() {
        return "SELECT EXISTS (" +
                "SELECT 1 FROM comments " +
                "WHERE id=:id)";
    }
}
