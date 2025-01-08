package com.forum.project.infrastructure.persistence.tag;

import com.forum.project.domain.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepositoryJdbcImpl implements TagRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<Tag> findById(Long id) {
        String sql = "SELECT * FROM tags WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Tag.class))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Tag> findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = :name";
        SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
        return Optional.ofNullable(jdbcTemplate
                .queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(Tag.class)));
    }

    @Override
    public List<Tag> findByNameContainingIgnoreCase(String keyword) {
        String sql = "SELECT * FROM tags WHERE LOWER(name) LIKE :keyword";
        SqlParameterSource namedParameters = new MapSqlParameterSource("keyword", "%" + keyword.toLowerCase() + "%");
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Tag.class));
    }

    @Override
    public List<Tag> findAllById(List<Long> tagIds) {
        String sql = "SELECT * FROM tags WHERE id IN (:tagIds)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("tagIds", tagIds);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Tag.class));
    }

    @Override
    public List<Tag> findAll() {
        String sql = "SELECT * FROM tags";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tag.class));
    }

    @Override
    public Tag save(Tag tag) {
        String sql = "INSERT INTO tags (name) VALUES (:name)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(tag);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, new String[]{"id"});
        tag.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return tag;
    }

    @Override
    public List<Tag> findTagsByQuestionId(Long questionId) {
        String sql = "SELECT t.* FROM tags t " +
                "JOIN question_tag qt ON t.id = qt.tag_id " +
                "WHERE qt.question_id = :questionId";

        SqlParameterSource namedParameters = new MapSqlParameterSource("questionId", questionId);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(Tag.class));
    }

    public boolean existsByName(String name) {
        String sql = "SELECT EXISTS (SELECT * FROM tags WHERE name = :name)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
