package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.report.dto.CommentReportCreateDto;
import com.forum.project.domain.report.entity.CommentReport;
import com.forum.project.domain.report.repository.CommentReportRepository;
import com.forum.project.infrastructure.persistence.key.CommentReportKey;
import com.forum.project.infrastructure.persistence.queries.CommentReportQueries;
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
public class CommentReportJdbcImpl implements CommentReportRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private SqlParameterSource createSqlParameterSource(CommentReportCreateDto dto) {
        return new MapSqlParameterSource(Map.of(
                "userId", dto.getUserId(),
                "commentId", dto.getCommentId(),
                "reason", dto.getReason().name(),
                "status", dto.getStatus().name(),
                "details", dto.getDetails()
        ));
    }

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(CommentReportCreateDto dto) {
        String sql = CommentReportQueries.insert();
        SqlParameterSource namedParameters = createSqlParameterSource(dto);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, CommentReportKey.getKeys());
        return keyHolder.getKeys();
    }

    @Override
    public boolean existsByCommentIdAndUserId(Long commentId, Long userId) {
        String sql = CommentReportQueries.existsByCommentIdAndUserId();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("commentId", commentId)
                .addValue("userId", userId);
        Boolean result = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public Long countByCommentId(Long commentId) {
        String sql = CommentReportQueries.countByCommentId();
        SqlParameterSource params = new MapSqlParameterSource("commentId", commentId);
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    @Override
    public void delete(Long id) {
        String sql = CommentReportQueries.delete();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<CommentReport> findAllByCommentId(Long commentId) {
        String sql = CommentReportQueries.findAllByCommentId();
        SqlParameterSource params = new MapSqlParameterSource("commentId", commentId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CommentReport.class));
    }

    @Override
    public List<CommentReport> findAllByUserId(Long userId) {
        String sql = CommentReportQueries.findAllByUserId();
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CommentReport.class));
    }

    @Override
    public Optional<CommentReport> findById(Long id) {
        String sql = CommentReportQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            CommentReport result = jdbcTemplate.queryForObject(
                    sql, params, new BeanPropertyRowMapper<>(CommentReport.class)
            );
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
}
