package com.forum.project.infrastructure.persistence.bookmark;

public class BookmarkQueries {
    public static String findByQuestionIdAndUserId() {
        return "SELECT * FROM bookmarks " +
               "WHERE user_id=:userId AND question_id=:questionId;";
    }

    public static String insert() {
        return "INSERT INTO bookmarks " +
               "(user_id, question_id, notes) " +
               "VALUES (:userId, :questionId, :notes)";
    }

    public static String delete() {
        return "DELETE FROM bookmarks " +
               "WHERE user_id=:userId AND question_id=:questionId";
    }

    public static String findAllByUserId() {
        return "SELECT * FROM bookmarks " +
               "WHERE user_id=:userId;";
    }

    public static String findById() {
        return "SELECT * FROM bookmarks " +
                "WHERE id=:id;";
    }

    public static String existsByUserIdAndQuestionId() {
        return "SELECT EXISTS (" +
                "SELECT 1 FROM bookmarks " +
                "WHERE user_id=:userId " +
                "AND question_id=:questionId" +
                ");";
    }
}
