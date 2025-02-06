package com.forum.project.infrastructure.persistence;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Optional;

public class JdbcTestUtils {

    public static void createTable(NamedParameterJdbcTemplate jdbcTemplate, String tableName, String columns) {
        String sql = String.format("CREATE TABLE %s (%s);", tableName, columns);
        jdbcTemplate.getJdbcTemplate().execute(sql);
    }

    public static void dropTable(NamedParameterJdbcTemplate jdbcTemplate, String tableName) {
        String sql = String.format("DROP TABLE IF EXISTS %s;", tableName);
        jdbcTemplate.getJdbcTemplate().execute(sql);
    }

    public static <T> Optional<T> findData(NamedParameterJdbcTemplate jdbcTemplate, String sql, Long id, Class<T> clazz) {
        try {
            T result = jdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id)
                    ,new BeanPropertyRowMapper<>(clazz));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
