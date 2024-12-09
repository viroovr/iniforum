package com.forum.project.infrastructure;

import com.forum.project.domain.entity.Question;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.CustomDatabaseException;
import com.forum.project.domain.repository.QuestionRepository;
import com.forum.project.domain.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final Clock clock;

    private final RowMapper<Question> questionRowMapper = (rs, rowNum) -> new Question(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("user_id"),
            rs.getString("content"),
            rs.getString("tag"),
            rs.getTimestamp("created_date").toLocalDateTime()
    );

    public Optional<Question> findById(Long id) {
        String sql = "SELECT * FROM questions WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate
                .query(sql, namedParameters, new BeanPropertyRowMapper<>(Question.class))
                .stream()
                .findFirst();
    }

    public Question save(Question question) {
        if (question.getCreatedDate() == null) {
            question.setCreatedDate(LocalDateTime.now(clock));
        }
        question.setViewCount(0);
        String sql = "INSERT INTO questions (title, user_id, content, tag, created_date) VALUES (:title, :userId, :content, :tag, :createdDate)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(question);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        question.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return question;

    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM questions WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, namedParameters);
    }


    private boolean questionExists(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }

    private boolean userIdExists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM questions";
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class))
                .orElse(0L);
    }

    public List<Question> getQuestionByPage(int page, int size) {
        int offset = page * size;
        String sql = "SELECT * FROM questions ORDER BY created_date DESC LIMIT :size OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("offset", offset);
        return namedParameterJdbcTemplate.query(sql, params, questionRowMapper);
    }

    @Override
    public List<Question> searchQuestions(String keyword, int page, int size) {
        int offset = page * size;
        String sql =
                "SELECT * FROM questions WHERE LOWER(title) LIKE LOWER(:keyword)"
                +"OR LOWER(content) LIKE LOWER(:keyword)"
                +"ORDER BY created_date DESC LIMIT :size OFFSET :offset";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("keyword", "%" + keyword + "%");
        parameterSource.addValue("limit", size);
        parameterSource.addValue("offset", offset);

        return namedParameterJdbcTemplate.query(sql, parameterSource, questionRowMapper);
    }

    @Override
    public void updateViewCount(Long questionId, Integer viewCount) {
        String updateSql = "UPDATE Question q SET q.viewCount = q.viewCount + :viewCount WHERE q.id = :questionId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("questionId", questionId);
        parameterSource.addValue("viewCount", viewCount);

        namedParameterJdbcTemplate.update(updateSql, parameterSource);
    }
}
