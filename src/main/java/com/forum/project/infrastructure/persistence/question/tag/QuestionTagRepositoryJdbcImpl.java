package com.forum.project.infrastructure.persistence.question.tag;

import com.forum.project.domain.question.tag.QuestionTag;
import com.forum.project.domain.question.tag.QuestionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionTagRepositoryJdbcImpl implements QuestionTagRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private MapSqlParameterSource createParams(String key, Object value) {
        return new MapSqlParameterSource(key, value);
    }

    private int updateQuery(String sql, SqlParameterSource params) {
        return jdbcTemplate.update(sql, params);
    }

    private <T> Optional<T> queryForObject(String sql, SqlParameterSource params, Class<T> type) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(type)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private <T> List<T> queryForList(String sql, SqlParameterSource params, Class<T> type) {
        return jdbcTemplate.queryForList(sql, params, type);
    }

    @Override
    public int insert(QuestionTag questionTag) {
        return updateQuery(QuestionTagQueries.insert(), new BeanPropertySqlParameterSource(questionTag));
    }

    @Override
    public int[] saveAll(List<QuestionTag> questionTags) {
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(questionTags.toArray());
        return jdbcTemplate.batchUpdate(QuestionTagQueries.insert(), params);
    }

    @Override
    public Optional<QuestionTag> findByQuestionIdAndTagId(Long questionId, Long tagId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("tagId", tagId);
        return queryForObject(QuestionTagQueries.findByQuestionIdAndTagId(), params, QuestionTag.class);
    }

    @Override
    public List<Long> findTagIdsByQuestionId(Long questionId) {
        return queryForList(QuestionTagQueries.findTagIdsByQuestionId(), createParams("questionId", questionId), Long.class);
    }

    @Override
    public List<Long> findQuestionIdsByTagId(Long tagId) {
        return queryForList(QuestionTagQueries.findQuestionIdsByTagId(), createParams("tagId", tagId), Long.class);
    }

    @Override
    public void deleteByQuestionId(Long questionId) {
        updateQuery(QuestionTagQueries.deleteByQuestionId(), createParams("questionId", questionId));
    }

    @Override
    public void deleteByTagId(Long tagId) {
        updateQuery(QuestionTagQueries.deleteByTagId(), createParams("tagId", tagId));
    }
}
