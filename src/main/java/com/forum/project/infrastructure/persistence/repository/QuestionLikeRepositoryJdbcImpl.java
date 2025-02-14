package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.like.entity.QuestionLike;
import com.forum.project.domain.like.repository.QuestionLikeRepository;
import com.forum.project.infrastructure.persistence.queries.QuestionLikeQueries;
import com.forum.project.infrastructure.persistence.key.QuestionLikeKey;
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

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QuestionLikeRepositoryJdbcImpl implements QuestionLikeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(QuestionLike build) {
        String sql = QuestionLikeQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(build);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder, QuestionLikeKey.getKeys());
        return keyHolder.getKeys();
    }

    @Override
    public boolean existsByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.existsByQuestionIdAndUserId();
        SqlParameterSource sqlParameterSource = createSqlParameterSource(questionId, userId);
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, sqlParameterSource, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<QuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.findByQuestionIdAndUserId();
        SqlParameterSource sqlParameterSource = createSqlParameterSource(questionId, userId);
        try {
            QuestionLike result = namedParameterJdbcTemplate.queryForObject(
                    sql, sqlParameterSource, new BeanPropertyRowMapper<>(QuestionLike.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<QuestionLike> findById(Long id) {
        String sql = QuestionLikeQueries.findById();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("id", id);
        try {
            QuestionLike result = namedParameterJdbcTemplate.queryForObject(
                    sql, sqlParameterSource, new BeanPropertyRowMapper<>(QuestionLike.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.deleteByQuestionIdAndUserId();
        SqlParameterSource source = createSqlParameterSource(questionId, userId);
        namedParameterJdbcTemplate.update(sql, source);
    }

    private SqlParameterSource createSqlParameterSource(Long questionId, Long userId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);
    }
}
