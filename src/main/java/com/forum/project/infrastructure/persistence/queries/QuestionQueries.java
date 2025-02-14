package com.forum.project.infrastructure.persistence.queries;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionQueries {
    public static String insertAndReturnGeneratedKeys() {
        return "INSERT INTO questions " +
                "(user_id, title, content, status) " +
                "VALUES (:userId, :title, :content, :status)";
    }

    public static String findById() {
        return "SELECT * FROM questions " +
                "WHERE id = :id";
    }

    public static String findByIds() {
        return "SELECT * FROM questions " +
                "WHERE id IN (:ids)";
    }

    public static String findByUserId() {
        return "SELECT * FROM questions " +
                "WHERE user_id = :userId " +
                "ORDER BY created_date DESC " +
                "LIMIT :limit OFFSET :offset ";
    }

    public static String findByStatus() {
        return "SELECT * FROM questions " +
                "WHERE status = :status " +
                "ORDER BY created_date DESC " +
                "LIMIT :limit OFFSET :offset ";
    }

    public static String searchByColumns(List<String> columns) {
        return "SELECT * FROM questions WHERE " +
                columns.stream()
                        .map(c -> "LOWER(" + c + ") LIKE LOWER(:keyword)")
                        .collect(Collectors.joining(" OR ")) +
                " ORDER BY created_date DESC " +
                "LIMIT :limit OFFSET :offset ";
    }

    public static String deleteById() {
        return "DELETE FROM questions " +
                "WHERE id = :id";
    }

    public static String existsById() {
        return "SELECT EXISTS (SELECT 1 FROM questions " +
                "WHERE id =:id)";
    }

    public static String countAll() {
        return "SELECT COUNT(*) FROM questions";
    }

    public static String countByStatus() {
        return "SELECT COUNT(*) FROM questions " +
                "WHERE status =:status";
    }

    public static String countByUserId() {
        return "SELECT COUNT(*) FROM questions " +
                "WHERE user_id =:userId";
    }

    public static String countByQuestionIds() {
        return "SELECT COUNT(*) FROM questions " +
                "WHERE id IN (:ids)";
    }

    public static String countByColumnsKeyword(List<String> columns) {
        return "SELECT COUNT(*) FROM questions WHERE " +
                columns.stream()
                        .map(col -> "LOWER( "+ col + ") LIKE LOWER(:keyword)")
                        .collect(Collectors.joining(" OR "));
    }

    public static String getByPage() {
        return "SELECT * FROM questions " +
                "ORDER BY created_date DESC " +
                "LIMIT :limit OFFSET :offset ";
    }

    public static String getByPageable(Pageable pageable) {
        return "SELECT * FROM questions " +
                "ORDER BY " +
                pageable.getSort().stream()
                        .map(order -> order.getProperty() + " " + order.getDirection())
                        .collect(Collectors.joining(", ")) +
                " LIMIT :limit OFFSET :offset ";
    }


    public static String getViewCountById() {
        return "SELECT view_count FROM questions " +
                "WHERE id =:id";
    }

    public static String updateViewCount() {
        return "UPDATE questions q SET q.view_count = q.view_count + :plus " +
                "WHERE q.id = :id";
    }

    public static String updateUpVotedCount() {
        return "UPDATE questions q SET q.up_voted_count = q.up_voted_count + :delta " +
                "WHERE q.id = :id";
    }

    public static String updateDownVotedCount() {
        return "UPDATE questions q SET q.down_voted_count = q.down_voted_count + :delta " +
                "WHERE q.id = :id";
    }

    public static String updateTitleAndContent() {
        return "UPDATE questions " +
                "SET title =:title, content =:content, last_modified_date = CURRENT_TIMESTAMP " +
                "WHERE id =:id";
    }
}
