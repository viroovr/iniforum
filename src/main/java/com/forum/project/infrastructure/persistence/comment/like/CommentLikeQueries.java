package com.forum.project.infrastructure.persistence.comment.like;

public class CommentLikeQueries {

    public static String insertAndReturnGeneratedKeys() {
        return "INSERT INTO comment_likes " +
                "(user_id, comment_id, status, ip_address)" +
                "VALUES (:userId, :commentId, :status, :ipAddress)";
    }

    public static String findByUserIdAndCommentId() {
        return "SELECT * FROM comment_likes WHERE " +
                "user_id=:userId AND comment_id=:commentId";
    }

    public static String existsByCommentIdAndUserId() {
        return "SELECT EXISTS (SELECT 1 FROM comment_likes WHERE " +
                "user_id =:userId AND comment_id =:commentId)";
    }

    public static String findCommentIdsByUserIdAndStatus() {
        return "SELECT comment_id FROM comment_likes WHERE " +
                "user_id =:userId AND status =:status";
    }

    public static String delete() {
        return "DELETE FROM comment_likes WHERE id =:id";
    }

    public static String updateStatus() {
        return "UPDATE comment_likes SET status =:status WHERE id = :id";
    }

    public static String findById() {
        return "SELECT * FROM comment_likes WHERE id = :id";
    }
}
