package com.forum.project.infrastructure.persistence.question;

import com.forum.project.domain.question.Question;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final Clock clock;

    private static final String TABLE_NAME = "questions";

    public Optional<Question> findById(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE_NAME);
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
        String sql = "SELECT EXISTS (SELECT 1 FROM questions WHERE id = :id)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }

    private boolean userIdExists(String loginId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM questions WHERE login_id = :loginId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
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
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Question.class));
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

        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(Question.class));
    }

    @Override
    public void updateViewCount(Long questionId, Integer viewCount) {
        String updateSql = "UPDATE questions q SET q.viewCount = q.viewCount + :viewCount WHERE q.id = :questionId";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("viewCount", viewCount);
        parameterSource.addValue("questionId", questionId);

        namedParameterJdbcTemplate.update(updateSql, parameterSource);
    }

    @Override
    public Long getTotalUserQuestionCount(Long userId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE user_id=:userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Long.class))
                .orElse(0L);
    }

    @Override
    public List<Question> searchQuestionsByUser(Long userId, int page, int size) {
        int offset = page * size;
        String sql =
                "SELECT * FROM questions WHERE user_id = :userId"
                +"ORDER BY created_date DESC LIMIT :size OFFSET :offset";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("userId", userId);
        parameterSource.addValue("limit", size);
        parameterSource.addValue("offset", offset);
        return namedParameterJdbcTemplate.query(sql, parameterSource, new BeanPropertyRowMapper<>(Question.class));
    }
}
