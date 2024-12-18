package com.forum.project.infrastructure;

import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.UserRepository;
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
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final Clock clock;

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT 1 FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return Optional.ofNullable(namedParameterJdbcTemplate
                .queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(User.class)));
    }

    @Override
    public Optional<User> findByUserLoginId(String loginId) {
        String sql = "SELECT 1 FROM users WHERE login_id = :loginId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
        return Optional.ofNullable(namedParameterJdbcTemplate
                .queryForObject(sql, namedParameters, new BeanPropertyRowMapper<>(User.class)));
    }

    @Override
    public User save(User user) {
        if (user.getCreatedDate() == null) {
            user.setCreatedDate(LocalDateTime.now(clock));
        }
        String sql = "INSERT INTO users (login_id, email, password, name, nickname, created_date) " +
                "VALUES (:loginId, :email, :password, :name, :nickname, :createdDate)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    public User update(User user) {
        String sql = "UPDATE users SET password = :password, profile_image_path = :profileImagePath," +
                "nickname = :nickname WHERE id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("password", user.getPassword());
        parameterSource.addValue("profileImagePath", user.getProfileImagePath());
        parameterSource.addValue("nickname", user.getNickname());
        parameterSource.addValue("id", user.getId());

        namedParameterJdbcTemplate.update(sql, parameterSource);
        return user;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    public boolean userLoginIdExists(String loginId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE login_id = :loginId)";
        SqlParameterSource namedParameters = new MapSqlParameterSource("loginId", loginId);
        Boolean exists = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
