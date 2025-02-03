package com.forum.project.infrastructure.persistence.commentlike;

import com.forum.project.domain.like.commentlike.CommentLike;
import com.forum.project.domain.like.commentlike.CommentLikeKey;
import com.forum.project.domain.like.commentlike.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryJdbcImpl implements CommentLikeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(CommentLike commentLike) {
        String sql = CommentLikeQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new BeanPropertySqlParameterSource(commentLike);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, CommentLikeKey.getKeys());

        return keyHolder.getKeys();
    }

    @Override
    public boolean existsByUserIdAndCommentId(Long userId, Long commentId) {
        String sql = CommentLikeQueries.existsByCommentIdAndUserId();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("commentId", commentId);

        Boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId) {
        String sql = CommentLikeQueries.findByUserIdAndCommentId();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("commentId", commentId);
        try {
            CommentLike result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(CommentLike.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String sql = CommentLikeQueries.delete();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<Long> findCommentIdsByUserIdAndStatus(Long userId, String status) {
        String sql = CommentLikeQueries.findCommentIdsByUserIdAndStatus();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("status", status);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getLong(1));
    }

    @Override
    public int updateStatus(Long id, String status) {
        String sql = CommentLikeQueries.updateStatus();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("id", id);
        return jdbcTemplate.update(sql, params);
    }
}