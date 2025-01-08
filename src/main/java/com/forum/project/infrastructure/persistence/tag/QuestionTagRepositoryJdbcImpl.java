package com.forum.project.infrastructure.persistence.tag;

import com.forum.project.domain.tag.QuestionTag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionTagRepositoryJdbcImpl implements QuestionTagRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void save(QuestionTag questionTag) {
        String sql = "INSERT INTO question_tags (question_id, tag_id) VALUES (:questionId, :tagId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("questionId", questionTag.getQuestionId())
                .addValue("tagId", questionTag.getTagId());

        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public List<Long> findTagIdsByQuestionId(Long questionId) {
        String sql = "SELECT tag_id FROM question_tags WHERE question_id = :questionId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("questionId", questionId);

        return jdbcTemplate.queryForList(sql, namedParameters, Long.class);
    }

    @Override
    public List<Long> findQuestionIdsByTagId(Long tagId) {
        String sql = "SELECT question_id FROM question_tag WHERE tag_id = :tagId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("tagId", tagId);

        return jdbcTemplate.queryForList(sql, namedParameters, Long.class);
    }

    @Override
    public void deleteByQuestionId(Long questionId) {
        String sql = "DELETE FROM question_tag WHERE question_id = :questionId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("questionId", questionId);

        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void deleteByTagId(Long tagId) {
        String sql = "DELETE FROM question_tag WHERE tag_id = :tagId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("tagId", tagId);

        jdbcTemplate.update(sql, namedParameters);
    }
}
