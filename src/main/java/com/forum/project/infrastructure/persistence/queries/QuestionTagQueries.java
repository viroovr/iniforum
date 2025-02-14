package com.forum.project.infrastructure.persistence.queries;

public class QuestionTagQueries {
    public static String insert() {
        return "INSERT INTO question_tags (question_id, tag_id) VALUES (:questionId, :tagId)";
    }

    public static String findTagIdsByQuestionId() {
        return "SELECT tag_id FROM question_tags WHERE question_id =:questionId";
    }

    public static String findQuestionIdsByTagId() {
        return "SELECT question_id FROM question_tags WHERE tag_id =:tagId";
    }

    public static String findByQuestionIdAndTagId() {
        return "SELECT * FROM question_tags WHERE question_id =:questionId AND " +
                "tag_id = :tagId";
    }

    public static String deleteByQuestionId() {
        return "DELETE FROM question_tags WHERE question_id =:questionId";
    }

    public static String deleteByTagId() {
        return "DELETE FROM question_tags WHERE tag_id =:tagId";
    }
}
