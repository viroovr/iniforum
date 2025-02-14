package com.forum.project.infrastructure.persistence.queries;

public class QuestionLikeQueries {
    public static String insertAndReturnGeneratedKeys() {
        return "INSERT INTO question_likes " +
                "(user_id, status, ip_address, question_id) " +
                "VALUES (:userId, :status, :ipAddress, :questionId)";
    }

    public static String existsByQuestionIdAndUserId() {
        return "SELECT EXISTS (SELECT 1 FROM question_likes " +
                "WHERE question_id = :questionId AND user_id = :userId)";
    }

    public static String findById() {
        return "SELECT * FROM question_likes WHERE id = :id";
    }

    public static String findByQuestionIdAndUserId() {
        return "SELECT * FROM question_likes " +
                "WHERE question_id = :questionId AND user_id = :userId";
    }

    public static String deleteByQuestionIdAndUserId() {
        return "DELETE FROM question_likes " +
                "WHERE question_id = :questionId AND user_id = :userId";
    }
}
