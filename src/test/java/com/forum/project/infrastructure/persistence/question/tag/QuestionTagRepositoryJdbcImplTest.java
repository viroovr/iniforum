package com.forum.project.infrastructure.persistence.question.tag;

import com.forum.project.domain.question.tag.QuestionTag;
import com.forum.project.domain.question.tag.QuestionTagRepository;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@Slf4j
@ActiveProfiles("test")
class QuestionTagRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private QuestionTagRepository questionTagRepository;

    @BeforeEach
    void setUp() {
        questionTagRepository = new QuestionTagRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "question_tags");
        JdbcTestUtils.createTable(jdbcTemplate,
                "question_tags",
                "question_id BIGINT NOT NULL, " +
                        "tag_id BIGINT NOT NULL, " +
                        "PRIMARY KEY (question_id, tag_id) ");
    }

    private QuestionTag createQuestionTag(Long questionId, Long tagId) {
        return QuestionTag.builder()
                .questionId(questionId)
                .tagId(tagId)
                .build();
    }

    private void insertData(Long questionId, Long tagId) {
        String sql = QuestionTagQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("tagId", tagId);
        jdbcTemplate.update(sql, params);
    }

    private Optional<QuestionTag> findData(Long questionId, Long tagId) {
        String sql = QuestionTagQueries.findByQuestionIdAndTagId();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("questionId", questionId)
                .addValue("tagId", tagId);
        try {
            QuestionTag result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(QuestionTag.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Test
    void insert() {
        QuestionTag questionTag = createQuestionTag(1L, 1L);

        int result = questionTagRepository.insert(questionTag);

        assertThat(result).isOne();
    }

    @Test
    void saveAll() {
        QuestionTag questionTag = createQuestionTag(1L, 1L);
        QuestionTag questionTag1 = createQuestionTag(2L, 2L);

        int[] result = questionTagRepository.saveAll(List.of(questionTag, questionTag1));

        assertThat(result).hasSize(2)
                .containsOnly(1);
    }

    @Test
    void findTagIdsByQuestionId() {
        insertData(1L, 1L);
        insertData(1L, 999L);
        insertData(2L, 2L);

        List<Long> result = questionTagRepository.findTagIdsByQuestionId(1L);

        assertThat(result).hasSize(2)
                .containsExactlyInAnyOrder(1L, 999L);
    }

    @Test
    void findQuestionIdsByTagId() {
        insertData(1L, 1L);
        insertData(1L, 2L);
        insertData(999L, 2L);

        List<Long> result = questionTagRepository.findQuestionIdsByTagId(2L);

        assertThat(result).hasSize(2)
                .containsExactlyInAnyOrder(1L, 999L);
    }

    @Test
    void deleteByQuestionId() {
        insertData(1L, 1L);
        assertThat(findData(1L, 1L)).isPresent();

        questionTagRepository.deleteByQuestionId(1L);
        assertThat(findData(1L, 1L)).isEmpty();

        assertThatCode(() -> questionTagRepository.deleteByQuestionId(999L)).doesNotThrowAnyException();
    }

    @Test
    void deleteByTagId() {
        insertData(1L, 999L);
        assertThat(findData(1L, 999L)).isPresent();

        questionTagRepository.deleteByTagId(999L);
        assertThat(findData(1L, 999L)).isEmpty();

        assertThatCode(() -> questionTagRepository.deleteByQuestionId(1234L)).doesNotThrowAnyException();
    }
}