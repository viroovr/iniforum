package com.forum.project.infrastructure.persistence.queries;

public class TagQueries {
    public static String insert() {
        return "INSERT INTO tags (name, usage_count) VALUES (:name, :usageCount)";
    }

    public static String findById() {
        return "SELECT * FROM tags WHERE id = :id";
    }

    public static String findByIds() {
        return "SELECT * FROM tags WHERE id IN (:ids)";
    }

    public static String findByName() {
        return "SELECT * FROM tags WHERE name = :name";
    }

    public static String findTagsByQuestionId() {
        return "SELECT t.* FROM tags t " +
                "JOIN question_tags qt ON qt.tag_id = t.id " +
                "WHERE qt.question_id = :questionId";
    }

    public static String findByNames() {
        return "SELECT * FROM tags WHERE " +
                "name IN (:names)";
    }

    public static String getByPage() {
        return "SELECT * FROM tags LIMIT :limit OFFSET :offset";
    }

    public static String searchByName() {
        return "SELECT * FROM tags " +
                "WHERE LOWER(name) LIKE '%' || LOWER(:keyword) || '%' " +
                "LIMIT :limit OFFSET :offset";
    }

    public static String searchByNames() {
        return "SELECT * FROM tags WHERE ";
    }

    public static String existsByName() {
        return "SELECT EXISTS (SELECT 1 FROM tags " +
                "WHERE name = :name)";
    }

    public static String updateName() {
        return "UPDATE tags SET name = :name, last_modified_date = CURRENT_TIMESTAMP " +
                "WHERE id = :id";
    }

    public static String delete() {
        return "DELETE FROM tags WHERE id = :id";
    }
}
