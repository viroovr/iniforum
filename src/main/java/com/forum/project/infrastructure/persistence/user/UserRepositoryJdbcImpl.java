package com.forum.project.infrastructure.persistence.user;

import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserKey;
import com.forum.project.domain.user.UserRepository;
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

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> findById(Long id) {
        String sql = UserQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            User result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        String sql = UserQueries.findByLoginId();
        SqlParameterSource params = new MapSqlParameterSource("loginId", loginId);
        try {
            User result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = UserQueries.findByEmail();
        SqlParameterSource params = new MapSqlParameterSource("email", email);
        try {
            User result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAllByLastActivityDateBefore(LocalDateTime thresholdDate) {
        String sql = UserQueries.findAllByLastActivityDateBefore();
        SqlParameterSource namedParameters = new MapSqlParameterSource("thresholdDate", thresholdDate);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public Map<String, Object> insertAndReturnGeneratedKeys(User user) {
        String sql = UserQueries.insertAndReturnGeneratedKeys();
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, UserKey.getKeys());

        return keyHolder.getKeys();
    }

    @Override
    public int updateProfile(User user) {
        String sql = UserQueries.updateProfile();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("password", user.getPassword())
                .addValue("profileImagePath", user.getProfileImagePath())
                .addValue("nickname", user.getNickname())
                .addValue("status", user.getStatus())
                .addValue("id", user.getId());

        return jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public int updateAllStatus(List<Long> userIds, List<String> statuses) {
        StringBuilder sql = new StringBuilder(UserQueries.updateAllStatus());
        MapSqlParameterSource params = new MapSqlParameterSource();

        for (int i = 0; i < userIds.size(); i++) {
            String statusParam = "status" + i;
            String idParam = "id" + i;

            sql.append("WHEN id = :").append(idParam).append(" THEN :").append(statusParam).append(" ");

            params.addValue(statusParam, statuses.get(i));
            params.addValue(idParam, userIds.get(i));
        }

        sql.append("END WHERE id IN (:ids)");
        params.addValue("ids", userIds);

        return jdbcTemplate.update(sql.toString(), params);
    }

    @Override
    public List<User> searchByLoginIdAndStatus(String keyword, String status, int page, int size) {
        String sql = UserQueries.searchByLoginIdAndStatus();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("keyword", keyword)
                .addValue("status", status)
                .addValue("limit", size)
                .addValue("offset", size * page);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public Long countByLoginIdAndStatus(String keyword, String status) {
        String sql = UserQueries.countByKeywordAndStatus();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("keyword", keyword)
                .addValue("status", status);
        Long result = jdbcTemplate.queryForObject(sql, params, Long.class);
        return Optional.ofNullable(result).orElse(0L);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = UserQueries.existsByEmail();
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        String sql = UserQueries.existsByLoginId();
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = UserQueries.existsById();
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public String getLoginIdById(Long id) {
        String sql = UserQueries.getLoginIdById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> rs.getString("login_id"));
    }

    @Override
    public void delete(Long id) {
        String sql = UserQueries.delete();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
