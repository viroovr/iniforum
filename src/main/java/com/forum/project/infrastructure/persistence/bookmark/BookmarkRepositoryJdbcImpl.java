package com.forum.project.infrastructure.persistence.bookmark;

import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkRepository;
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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryJdbcImpl implements BookmarkRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Clock clock;

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

    private void setDefaultTimestamps(Bookmark bookmark) {
        LocalDateTime now = LocalDateTime.now(clock);
        bookmark.setCreatedDate(Optional.ofNullable(bookmark.getCreatedDate()).orElse(now));
    }

    @Override
    public Bookmark insert(Bookmark bookmark) {
        String sql = BookmarkQueries.insert();
        setDefaultTimestamps(bookmark);

        SqlParameterSource source = new BeanPropertySqlParameterSource(bookmark);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, source, keyHolder, new String[] {"id"});
        bookmark.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookmark;
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
