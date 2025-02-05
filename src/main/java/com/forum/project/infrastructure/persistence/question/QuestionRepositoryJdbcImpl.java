package com.forum.project.infrastructure.persistence.question;

import com.forum.project.domain.question.Question;
import com.forum.project.domain.question.QuestionKey;
import com.forum.project.domain.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryJdbcImpl implements QuestionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(Question question) {
        String sql = QuestionQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(question);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, QuestionKey.getKeys());

        return keyHolder.getKeys();
    }

    @Override
    public Optional<Question> findById(Long id) {
        String sql = QuestionQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Question result =  jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(Question.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Question> findQuestionByIds(List<Long> ids) {
        String sql = QuestionQueries.findByIds();
        SqlParameterSource params = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public void deleteById(Long id) {
        String sql = QuestionQueries.deleteById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = QuestionQueries.existsById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public List<Question> findByUserId(Long userId, int page, int size) {
        String sql = QuestionQueries.findByUserId();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("limit", size)
                .addValue("offset", page * size);
        return jdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public List<Question> findByStatus(String status, int page, int size) {
        String sql = QuestionQueries.findByStatus();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", status)
                .addValue("limit", size)
                .addValue("offset", page * size);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Question.class));
    }

    private List<Question> searchByColumns(List<String> columns, String keyword, int page, int size) {
        String sql = QuestionQueries.searchByColumns(columns);
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("keyword", "%" + keyword + "%")
                .addValue("limit", size)
                .addValue("offset", page * size);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public List<Question> searchByTitle(String keyword, int page, int size) {
        return searchByColumns(Collections.singletonList("title"), keyword, page, size);
    }

    @Override
    public List<Question> searchByContent(String keyword, int page, int size) {
        return searchByColumns(Collections.singletonList("content"), keyword, page, size);
    }

    @Override
    public List<Question> searchByTitleOrContent(String keyword, int page, int size) {
        return searchByColumns(Arrays.asList("title", "content"), keyword, page, size);
    }

    @Override
    public List<Question> getByPageable(Pageable pageable) {
        String sql = QuestionQueries.getByPageable(pageable);
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());
        return jdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public List<Question> getByPage(int page, int size) {
        String sql = QuestionQueries.getByPage();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", size)
                .addValue("offset", page * size);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public Long getViewCountById(Long id) {
        String sql = QuestionQueries.getViewCountById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> rs.getLong("view_count"));
    }

    @Override
    public Long countAll() {
        String sql = QuestionQueries.countAll();
        Long result = jdbcTemplate.queryForObject(sql, new EmptySqlParameterSource(), Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public Long countByStatus(String status) {
        String sql = QuestionQueries.countByStatus();
        SqlParameterSource params = new MapSqlParameterSource("status", status);
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    private Long countByColumnsKeyword(List<String> columns, String keyword) {
        String sql = QuestionQueries.countByColumnsKeyword(columns);
        SqlParameterSource params = new MapSqlParameterSource("keyword", "%" + keyword + "%");
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public Long countByTitleKeyword(String keyword) {
        return countByColumnsKeyword(Collections.singletonList("title"), keyword);
    }

    @Override
    public Long countByContentKeyword(String keyword) {
        return countByColumnsKeyword(Collections.singletonList("content"), keyword);
    }

    @Override
    public Long countByContentOrTitleKeyword(String keyword) {
        return countByColumnsKeyword(List.of("content", "title"), keyword);
    }

    @Override
    public Long countByUserId(Long userId) {
        String sql = QuestionQueries.countByUserId();
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public Long countByQuestionIds(List<Long> ids) {
        String sql = QuestionQueries.countByQuestionIds();
        SqlParameterSource params = new MapSqlParameterSource("ids", ids);
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public int updateViewCount(Long id, Long plus) {
        String sql = QuestionQueries.updateViewCount();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("plus", plus)
                .addValue("id", id);
        return jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public int updateTitleAndContent(Long id, String title, String content) {
        String sql = QuestionQueries.updateTitleAndContent();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", title)
                .addValue("content", content);
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int updateUpVotedCount(Long id, Long delta) {
        String sql = QuestionQueries.updateUpVotedCount();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("delta", delta)
                .addValue("id", id);
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int updateDownVotedCount(Long id, Long delta) {
        String sql = QuestionQueries.updateDownVotedCount();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("delta", delta)
                .addValue("id", id);
        return jdbcTemplate.update(sql, params);
    }
}
