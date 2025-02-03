package com.forum.project.infrastructure.persistence.question.report;

public class QuestionReportQueries {
    public static String insertAndReturnGeneratedKeys() {
        return "INSERT INTO question_reports " +
                "(user_id, question_id, reason, status) " +
                "VALUES (:userId, :questionId, :reason, :status)";
    }

    public static String existsByQuestionIdAndUserId() {
        return "SELECT EXISTS (SELECT 1 FROM question_reports " +
                "WHERE question_id =:questionId AND user_id =:userId)";
    }

    public static String findById() {
        return "SELECT * FROM question_reports " +
                "WHERE id =:id";
    }

    public static String findAllByUserId() {
        return "SELECT * FROM question_reports " +
                "WHERE user_id =:userId";
    }

    public static String findAllByQuestionId() {
        return "SELECT * FROM question_reports " +
                "WHERE question_id =:questionId";
    }

    public static String countByQuestionId() {
        return "SELECT COUNT(*) FROM question_reports " +
                "WHERE question_id =:questionId";
    }
}
