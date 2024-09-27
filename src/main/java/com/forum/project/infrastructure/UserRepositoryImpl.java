package com.forum.project.infrastructure;

import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.domain.exception.EmailAlreadyExistsException;
import com.forum.project.domain.exception.UserIdAlreadyExistException;
import com.forum.project.domain.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("user_id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name")
    );
    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(sql, namedParameters, userRowMapper).stream()
                .findFirst()
                .orElseThrow(()-> new UserNotFoundException("해당 아이디 키를 찾을 수 없습니다."));
    }
    
    
    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        return namedParameterJdbcTemplate.query(sql, namedParameters, userRowMapper).stream()
                .findFirst()
                .orElseThrow(()-> new UserNotFoundException("해당 이메일을 찾을 수 없습니다."));
    }

    @Override
    public User findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        return namedParameterJdbcTemplate.query(sql, namedParameters, userRowMapper).stream()
                .findFirst()
                .orElseThrow(()-> new UserNotFoundException("해당 유저아이디를 찾을 수 없습니다."));
    }

    @Override
    public User save(User user) {
        if (emailExists(user.getEmail())) {
            throw new EmailAlreadyExistsException("이미 존재하는 이메일입니다.");
        }
        if (userIdExists(user.getUserId())) {
            throw new UserIdAlreadyExistException("이미 존재하는 아이디입니다.");
        }

        String sql = "INSERT INTO users (user_id, email, password, name) VALUES (:userId, :email, :password, :name)";
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = :email";
        SqlParameterSource namedParameters = new MapSqlParameterSource("email", email);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }

    private boolean userIdExists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        return count != null && count > 0;
    }
}