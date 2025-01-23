package com.forum.project.infrastructure.persistence.question;

public class QuestionLikeQueries {
    public static final String INSERT = """
        INSERT INTO question_likes
        (user_id, status, ip_address, created_date, question_id)
        VALUES (:userId, :status, :ipAddress, :createdDate, :questionId)
        """.stripIndent();

    public static final String EXISTS_BY_QUESTION_ID_AND_USER_ID = """
        SELECT EXISTS
        (SELECT * FROM question_likes
        WHERE question_id = :questionId
        AND user_id = :userId)
    """;

    public static final String FIND_BY_QUESTION_ID_AND_USER_ID = """
        SELECT * FROM question_likes
        WHERE question_id = :questionId
        AND user_id = :userId
    """;

    public static final String DELETE_BY_QUESTION_ID_AND_USER_ID = """
        DELETE FROM question_likes
        WHERE question_id = :questionId
        AND user_id = :userId
    """;
}
