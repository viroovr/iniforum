package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.report.entity.QuestionReport;
import com.forum.project.domain.report.repository.QuestionReportRepository;
import com.forum.project.infrastructure.persistence.queries.QuestionReportQueries;
import com.forum.project.infrastructure.persistence.key.QuestionReportKey;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionReportRepositoryJdbcImpl implements QuestionReportRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(QuestionReport report) {
        String sql = QuestionReportQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new BeanPropertySqlParameterSource(report);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, QuestionReportKey.getKeys());
        return keyHolder.getKeys();
    }

    @Override
    public boolean existsByQuestionIdAndUserId(Long questionId, Long userId) {
        String sql = QuestionReportQueries.existsByQuestionIdAndUserId();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("userId", userId);
        Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Optional<QuestionReport> findById(Long id) {
        String sql = QuestionReportQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            QuestionReport result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(QuestionReport.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<QuestionReport> findAllByUserId(Long userId) {
        String sql = QuestionReportQueries.findAllByUserId();
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(QuestionReport.class));
    }

    @Override
    public List<QuestionReport> findAllByQuestionId(Long questionId) {
        String sql = QuestionReportQueries.findAllByQuestionId();
        SqlParameterSource params = new MapSqlParameterSource("questionId", questionId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(QuestionReport.class));
    }

    @Override
    public Long countByQuestionId(Long questionId) {
        String sql = QuestionReportQueries.countByQuestionId();
        SqlParameterSource params = new MapSqlParameterSource("questionId", questionId);
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public void delete(Long id) {
        String sql = QuestionReportQueries.delete();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
