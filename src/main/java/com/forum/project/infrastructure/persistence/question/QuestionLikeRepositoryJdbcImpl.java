package com.forum.project.infrastructure.persistence.question;

import com.forum.project.application.exception.InfraErrorCode;
import com.forum.project.application.exception.InfraException;
import com.forum.project.domain.question.like.QuestionLike;
import com.forum.project.domain.question.like.QuestionLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class QuestionLikeRepositoryJdbcImpl implements QuestionLikeRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Clock clock;

    @Override
    public QuestionLike insert(QuestionLike build) {
        String sql = QuestionLikeQueries.INSERT;
        build.setCreatedDate(Optional.ofNullable(build.getCreatedDate())
                .orElse(LocalDateTime.now(clock)));

        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(build);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder, new String[] {"id"});
        build.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return build;
    }

    @Override
    public boolean existsByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.EXISTS_BY_QUESTION_ID_AND_USER_ID;
        SqlParameterSource sqlParameterSource = createSqlParameterSource(questionId, userId);
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, sqlParameterSource, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<QuestionLike> findByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.FIND_BY_QUESTION_ID_AND_USER_ID;
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
    public void delete(Long questionId, Long userId) {
        String sql = QuestionLikeQueries.DELETE_BY_QUESTION_ID_AND_USER_ID;
        SqlParameterSource source = createSqlParameterSource(questionId, userId);
        namedParameterJdbcTemplate.update(sql, source);
    }

    private SqlParameterSource createSqlParameterSource(Long questionId, Long userId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);
    }
}
