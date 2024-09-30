package com.forum.project.infrastructure;

import com.forum.project.domain.Question;
import com.forum.project.domain.QuestionRepository;
import com.forum.project.domain.exception.QuestionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class QuestionRepositoryImpl implements QuestionRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Question> questionRowMapper = (rs, rowNum) -> new Question(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("user_id"),
            rs.getString("content"),
            rs.getString("tag"),
            rs.getTimestamp("created_date").toLocalDateTime()
    );

    public long count() {
        String sql = "SELECT COUNT(*) FROM questions";
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class))
                .orElse(0L);
    }

    public Question findById(Long id) {
        String sql = "SELECT * FROM questions WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(sql, namedParameters, questionRowMapper).stream()
                        .findFirst()
                        .orElseThrow(() -> new QuestionNotFoundException("질문이 존재하지 않습니다."));
    }

    public Question save(Question question) {
        if (question.getCreatedDate() == null) {
            question.setCreatedDate(LocalDateTime.now());
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

    public List<Question> getQuestionByPage(int page, int size) {
        int offset = page * size;
        String sql = "SELECT * FROM questions ORDER BY created_date DESC LIMIT :size OFFSET :offset";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("page", page);
        params.addValue("offset", offset);
        return namedParameterJdbcTemplate.query(sql, params, questionRowMapper);
    }

    @Override
    public Page<Question> searchQuestions(String keyword, Pageable pageable) {
        String sql =
                "SELECT * FROM questions WHERE LOWER(title) LIKE LOWER(:keyword)"
                +"OR LOWER(content) LIKE LOWER(:keyword)"
                +"ORDER BY created_date DESC LIMIT :limit OFFSET :offset";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("keyword", "%" + keyword + "%");
        parameterSource.addValue("limit", pageable.getPageSize());
        parameterSource.addValue("offset", pageable.getOffset());

        List<Question> questions = namedParameterJdbcTemplate.query(sql, parameterSource, questionRowMapper);

        String countSql = "SELECT COUNT(*) FROM questions WHERE title LIKE LOWER(:keyword) OR content LIKE LOWER(:keyword)";
        int total = Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(countSql, parameterSource, Integer.class))
                .orElse(0);

        return new PageImpl<>(questions, pageable, total);
    }

}
