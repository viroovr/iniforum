package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.tag.entity.Tag;
import com.forum.project.domain.tag.repository.TagRepository;
import com.forum.project.infrastructure.persistence.key.TagKey;
import com.forum.project.infrastructure.persistence.queries.TagQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class TagRepositoryJdbcImpl implements TagRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(Tag tag) {
        String sql = TagQueries.insert();
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(tag);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, TagKey.getKeys());

        return keyHolder.getKeys();
    }

    @Override
    public List<Map<String, Object>> saveAll(List<Tag> newTags) {
        String sql = TagQueries.insert();
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(newTags.toArray());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.batchUpdate(sql, params, keyHolder, TagKey.getKeys());

        return keyHolder.getKeyList();
    }

    private Optional<Tag> findSingleResult(String sql, SqlParameterSource params) {
        try {
            Tag result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(Tag.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return findSingleResult(TagQueries.findById(), new MapSqlParameterSource("id", id));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return findSingleResult(TagQueries.findByName(), new MapSqlParameterSource("name", name));
    }

    private List<Tag> executeQuery(String sql, SqlParameterSource params) {
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Tag.class));
    }

    private MapSqlParameterSource createPaginationParams(String keyword, int page, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", size)
                .addValue("offset", page * size);
        if (keyword != null) params.addValue("keyword",keyword);
        return params;
    }

    @Override
    public List<Tag> searchByName(String keyword, int page, int size) {
        return executeQuery(TagQueries.searchByName(), createPaginationParams(keyword, page, size));
    }

    @Override
    public List<Tag> findByIds(List<Long> ids) {
        return executeQuery(TagQueries.findByIds(), new MapSqlParameterSource("ids", ids));
    }

    @Override
    public List<Tag> getByPage(int page, int size) {
        return executeQuery(TagQueries.getByPage(), createPaginationParams(null, page, size));
    }

    @Override
    public List<Tag> findTagsByQuestionId(Long questionId) {
        return executeQuery(TagQueries.findTagsByQuestionId(),
                new MapSqlParameterSource("questionId", questionId));
    }

    @Override
    public List<Tag> findByNames(List<String> tagNames) {
        return executeQuery(TagQueries.findByNames(), new MapSqlParameterSource("names", tagNames));
    }

    @Override
    public List<Tag> searchByNames(List<String> tagNames, int page, int size) {
        StringBuilder sql = new StringBuilder(TagQueries.searchByNames());
        MapSqlParameterSource params = createPaginationParams(null, page, size);

        buildLikeConditions(tagNames, sql, params);

        return executeQuery(sql.toString(), params);
    }

    private void buildLikeConditions(List<String> tagNames, StringBuilder sql, MapSqlParameterSource params) {
        for (int i = 0; i < tagNames.size(); i++) {
            sql.append("LOWER(name) LIKE '%' || :").append("keyword").append(i).append(" || '%' ");

            params.addValue("keyword" + i, tagNames.get(i));

            if (i < tagNames.size() - 1) {
                sql.append("OR ");
            }
        }
        sql.append(" LIMIT :limit OFFSET :offset");
    }

    public boolean existsByName(String name) {
        String sql = TagQueries.existsByName();
        SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public int updateName(Long id, String name) {
        String sql = TagQueries.updateName();
        SqlParameterSource params = new MapSqlParameterSource("name", name)
                .addValue("id", id);
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(Long id) {
        String sql = TagQueries.delete();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
