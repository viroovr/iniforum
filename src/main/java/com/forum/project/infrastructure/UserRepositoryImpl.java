package com.forum.project.infrastructure;

import com.forum.project.domain.entity.User;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.domain.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate
                .query(sql, namedParameters, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        return namedParameterJdbcTemplate
                .query(sql, namedParameters, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (user_id, email, password, name, nickname, created_date) " +
                "VALUES (:userId, :email, :password, :name, :nickname, :createdDate)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    public User update(User user) {
        if (!userIdExists(user.getUserId())) {
            throw new UserNotFoundException("존재하지 않는 아이디입니다.");
        }
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
        String sql = "SELECT COUNT(*) FROM users WHERE email = :email";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }

    public boolean userIdExists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }
}
