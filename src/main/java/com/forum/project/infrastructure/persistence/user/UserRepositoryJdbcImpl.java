package com.forum.project.infrastructure.persistence.user;

import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJdbcImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Clock clock;

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByUserLoginId(String loginId) {
        String sql = "SELECT * FROM users WHERE login_id = :loginId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
        return Optional.ofNullable(jdbcTemplate
                .queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(User.class)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        return Optional.ofNullable(jdbcTemplate
                .queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(User.class)));
    }

    @Override
    public List<User> findAllByLastActivityDateBefore(LocalDateTime thresholdDate) {
        String sql = "SELECT * FROM users WHERE last_activity_date < :thresholdDate";

        SqlParameterSource namedParameters = new MapSqlParameterSource("thresholdDate", thresholdDate);

        return jdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users " +
                "(login_id, email, password, last_name, first_name, nickname, profile_image_path, status, role) "+
                "VALUES " +
                "(:loginId, :email, :password, :lastName, :firstName, :nickname, :profileImagePath, :status, :role)";

        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, namedParameters, keyHolder, new String[] {"id"});
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        String selectSql = "SELECT " +
                "last_activity_date, password_last_modified_date, last_login_date, created_date " +
                "FROM users " +
                "WHERE id = :id";
        SqlParameterSource selectParams = new MapSqlParameterSource("id", user.getId());

        Map<String, Object> result = jdbcTemplate.queryForMap(selectSql, selectParams);

        user.setLastActivityDate(((Timestamp) result.get("last_activity_date")).toLocalDateTime());
        user.setPasswordLastModifiedDate(((Timestamp) result.get("password_last_modified_date")).toLocalDateTime());
        user.setLastLoginDate(((Timestamp) result.get("last_login_date")).toLocalDateTime());
        user.setCreatedDate(((Timestamp) result.get("created_date")).toLocalDateTime());

        return user;
    }

    public User update(User user) {
        String sql = "UPDATE users " +
                "SET password = :password, profile_image_path = :profileImagePath," +
                "nickname = :nickname, status = :status " +
                "WHERE id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("password", user.getPassword());
        parameterSource.addValue("profileImagePath", user.getProfileImagePath());
        parameterSource.addValue("nickname", user.getNickname());
        parameterSource.addValue("status", user.getStatus());
        parameterSource.addValue("id", user.getId());

        jdbcTemplate.update(sql, parameterSource);
        return user;
    }

    public int[] updateAll(List<User> users) {
        String sql = "UPDATE users " +
                "SET status = :status " +
                "WHERE id = :id";
        List<SqlParameterSource> batchArgs = new ArrayList<>();

        for (User user : users) {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("status", user.getStatus())
                    .addValue("id", user.getId());
            batchArgs.add(namedParameters);
        }

        return jdbcTemplate.batchUpdate(sql, batchArgs.toArray(new SqlParameterSource[0]));
    }

    public List<User> searchByKeywordAndFilters(String keyword, String role, String status, int offset, int limit) {
        String sql = """
            SELECT * 
            FROM users  
            WHERE (:keyword IS NULL OR username LIKE CONCAT('%', :keyword, '%') OR email LIKE CONCAT('%', :keyword, '%'))
              AND (:role IS NULL OR role = :role)
              AND (:status IS NULL OR status = :status)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """;

        return jdbcTemplate.query(
                sql,
                Map.of(
                        "keyword", keyword,
                        "role", role,
                        "status", status,
                        "limit", limit,
                        "offset", offset
                ),
                new BeanPropertyRowMapper<>(User.class)
        );
    }

    @Override
    public Long countByKeywordAndFilters(String keyword, String role, String status) {
        return 0L;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT EXISTS (SELECT * FROM users WHERE email = :email)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    public boolean userLoginIdExists(String loginId) {
        String sql = "SELECT EXISTS (SELECT * FROM users WHERE login_id = :loginId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsById(Long userId) {
        String sql = "SELECT EXISTS (SELECT * FROM users WHERE user_id = :userId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        Boolean exists = jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public String getLoginId(Long userId) {
        return "";
    }
}
