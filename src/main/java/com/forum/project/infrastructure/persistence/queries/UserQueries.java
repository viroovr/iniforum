package com.forum.project.infrastructure.persistence.queries;

public class UserQueries {
    public static String insertAndReturnGeneratedKeys() {
        return "INSERT INTO users " +
                "(login_id, email, password, last_name, first_name, nickname, " +
                "profile_image_path, status, role) " +
                "VALUES " +
                "(:loginId, :email, :password, :lastName, :firstName, :nickname, " +
                ":profileImagePath, :status, :role)";
    }

    public static String findById() {
        return "SELECT * FROM users WHERE id = :id";
    }

    public static String findByLoginId() {
        return "SELECT * FROM users WHERE login_id = :loginId";
    }

    public static String findByEmail() {
        return "SELECT * FROM users WHERE email = :email";
    }

    public static String findAllByLastActivityDateBefore() {
        return "SELECT * FROM users WHERE last_activity_date < :thresholdDate";
    }

    public static String updateProfile() {
        return "UPDATE users SET " +
                "password =:password, profile_image_path =:profileImagePath, " +
                "nickname=:nickname, status=:status " +
                "WHERE id = :id";
    }

    public static String updateAllStatus() {
        return "UPDATE users SET status = CASE ";
    }

    public static String existsByEmail() {
        return "SELECT EXISTS (SELECT 1 FROM users WHERE email =:email)";
    }

    public static String existsByLoginId() {
        return "SELECT EXISTS (SELECT 1 FROM users WHERE login_id =:loginId)";
    }

    public static String existsById() {
        return "SELECT EXISTS (SELECT 1 FROM users WHERE id =:id)";
    }

    public static String getLoginIdById() {
        return "SELECT login_id FROM users WHERE id =:id";
    }

    public static String searchByLoginIdAndStatus() {
        return "SELECT * FROM users WHERE " +
                "(:keyword IS NULL OR LOWER(login_id) LIKE '%' || LOWER(:keyword) || '%') AND " +
                "(:status IS NULL OR status =:status) " +
                "ORDER BY created_date DESC " +
                "LIMIT :limit OFFSET :offset";
    }

    public static String countByKeywordAndStatus() {
        return "SELECT COUNT(*) FROM users " +
                "WHERE (:keyword IS NULL OR LOWER(login_id) LIKE '%' || LOWER(:keyword) || '%') AND " +
                "(:status IS NULL OR status =:status)";
    }

    public static String delete() {
        return "DELETE FROM users WHERE id = :id";
    }

}
