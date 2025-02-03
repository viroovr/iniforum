package com.forum.project.infrastructure.persistence.comment.report;

import com.forum.project.domain.comment.report.CommentReport;
import com.forum.project.domain.comment.report.CommentReportRepository;
import com.forum.project.domain.report.ReportKey;
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

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(CommentReport commentReport) {
        String sql = CommentReportQueries.insert();
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(commentReport);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, ReportKey.getKeys());
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
