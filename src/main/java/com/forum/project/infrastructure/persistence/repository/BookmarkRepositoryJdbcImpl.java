package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import com.forum.project.infrastructure.persistence.key.BookmarkKey;
import com.forum.project.infrastructure.persistence.queries.BookmarkQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryJdbcImpl implements BookmarkRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SqlParameterSource createSqlParameterSource(Long userId, Long questionId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);
    }

    @Override
    public Optional<Bookmark> findByUserIdAndQuestionId(Long userId, Long questionId) {
        String sql = BookmarkQueries.findByQuestionIdAndUserId();
        SqlParameterSource source = createSqlParameterSource(userId, questionId);
        try {
            Bookmark bookmark = namedParameterJdbcTemplate.queryForObject(
                    sql, source, new BeanPropertyRowMapper<>(Bookmark.class));
            return Optional.ofNullable(bookmark);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Bookmark> findAllByUserId(Long userId) {
        String sql = BookmarkQueries.findAllByUserId();
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Bookmark.class));
    }

    @Override
    public Map<String ,Object> insertAndReturnGeneratedKeys(Bookmark bookmark) {
        String sql = BookmarkQueries.insert();
        SqlParameterSource source = new BeanPropertySqlParameterSource(bookmark);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, source, keyHolder, BookmarkKey.getKeys());
        return keyHolder.getKeys();
    }

    @Override
    public void delete(Long userId, Long questionId) {
        String sql = BookmarkQueries.delete();
        SqlParameterSource params = createSqlParameterSource(userId, questionId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean existsByUserIdAndQuestionId(Long userId, Long questionId) {
        String sql = BookmarkQueries.existsByUserIdAndQuestionId();
        SqlParameterSource params = createSqlParameterSource(userId, questionId);
        Boolean result = namedParameterJdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}
