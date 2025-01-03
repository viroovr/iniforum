package com.forum.project.infrastructure.commentlike;

import com.forum.project.domain.commentlike.CommentLike;
import com.forum.project.domain.commentlike.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM likes WHERE user_id = :userId AND comment_id = :commentId)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("userId", userId);
        namedParameters.addValue("commentId", commentId);
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public CommentLike save(CommentLike commentLike) {
        String sql = "INSERT INTO likes (user_id, comment_id) VALUES (:userId, :commentId)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("userId", commentLike.getUserId());
        namedParameters.addValue("commentId", commentLike.getCommentId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        commentLike.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return commentLike;
    }
}
