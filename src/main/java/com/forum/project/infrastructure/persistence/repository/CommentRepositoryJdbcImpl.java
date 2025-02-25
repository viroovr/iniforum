package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.comment.dto.CommentCreateDto;
import com.forum.project.domain.comment.entity.Comment;
import com.forum.project.domain.comment.repository.CommentRepository;
import com.forum.project.infrastructure.persistence.key.CommentKey;
import com.forum.project.infrastructure.persistence.queries.CommentQueries;
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
public class CommentRepositoryJdbcImpl implements CommentRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<CommentKey> insertAndReturnGeneratedKeys(CommentCreateDto dto) {
        String sql = CommentQueries.insert();
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(dto);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int updated = jdbcTemplate.update(sql, namedParameters, keyHolder, CommentKey.getKeys());

        return updated > 0 ? Optional.of(new CommentKey(keyHolder.getKeys())) : Optional.empty();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = CommentQueries.findById();
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        try {
            Comment response = jdbcTemplate.queryForObject(
                    sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class));
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Comment> findAllByQuestionId(Long questionId) {
        String sql = CommentQueries.findAllByQuestionId();
        SqlParameterSource namedParameters = new MapSqlParameterSource("questionId", questionId);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class));
    }

    @Override
    public List<Comment> findAllByParentCommentId(Long parentCommentId) {
        String sql = CommentQueries.findAllByParentCommentId();
        SqlParameterSource namedParameters = new MapSqlParameterSource("parentCommentId", parentCommentId);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class));
    }

    @Override
    public List<Comment> findAllByUserId(Long userId) {
        String sql = CommentQueries.findAllByUserId();
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class));
    }

    @Override
    public int updateContent(Long id, String content) {
        String sql = CommentQueries.updateContent();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("content", content);

        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int updateUpVotedCount(Long id, Long delta) {
        String sql = CommentQueries.updateUpVotedCount();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("delta", delta);

        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int updateReportCount(Long id, Long delta) {
        return 0;
    }

    @Override
    public int updateDownVotedCount(Long id, Long delta) {
        String sql = CommentQueries.updateDownVotedCount();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("delta", delta);

        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int deleteById(Long id) {
        String sql = CommentQueries.deleteById();
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = CommentQueries.existsById();
        SqlParameterSource source = new MapSqlParameterSource("id", id);
        Boolean result = jdbcTemplate.queryForObject(sql, source, Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}
