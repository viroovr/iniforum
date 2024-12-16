package com.forum.project.infrastructure;

import com.forum.project.domain.entity.Comment;
import com.forum.project.domain.repository.CommentRepository;
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
public class CommentRepositoryImpl implements CommentRepository {

    private final Clock clock;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Comment> findByQuestionId(Long questionId) {
        String sql = "SELECT * FROM comments WHERE question_id = :questionId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("questionId", questionId);
        return namedParameterJdbcTemplate
                .query(sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class));
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate
                .query(sql, namedParameters, new BeanPropertyRowMapper<>(Comment.class))
                .stream()
                .findFirst();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getCreatedDate() == null) {
            comment.setCreatedDate(LocalDateTime.now(clock));
        }
        String sql = "INSERT INTO comments (user_id, content, created_date) VALUES (:userId, :content, :createdDate)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(comment);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        comment.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        String sql = "UPDATE comments SET content = :content WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", comment.getContent());
        params.addValue("id", comment.getId());

        namedParameterJdbcTemplate.update(sql, params);
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcTemplate.update(sql, namedParameters);
    }
}
