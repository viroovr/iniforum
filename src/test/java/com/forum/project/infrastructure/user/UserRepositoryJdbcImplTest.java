package com.forum.project.infrastructure.user;

import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.domain.user.UserRole;
import com.forum.project.domain.user.UserStatus;
import com.forum.project.infrastructure.persistence.user.UserRepositoryJdbcImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@JdbcTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/test-user-data.sql")
class UserRepositoryJdbcImplTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2024-12-19T00:00:00Z"), ZoneId.of("UTC"));
        userRepository = new UserRepositoryJdbcImpl(jdbcTemplate, fixedClock);
    }

    @AfterEach
    void tearDown() {
        // 현재 DB의 users 테이블 내용을 로그로 출력
        String selectQuery = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(selectQuery, new MapSqlParameterSource(), new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new User(
                        rs.getLong("id"),
                        rs.getString("login_id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("nickname"),
                        rs.getString("profile_image_path"),
                        rs.getString("status"),
                        rs.getString("role"),
                        rs.getTimestamp("created_date").toLocalDateTime(),
                        rs.getTimestamp("last_login_date").toLocalDateTime(),
                        rs.getTimestamp("password_last_modified_date").toLocalDateTime(),
                        rs.getTimestamp("last_activity_date").toLocalDateTime()
                );
            }
        });

        users.forEach(user -> log.info("User: {}", user));
    }


    @Test
    void shouldFindByIdSuccessfully_whenValidIdProvided() {
        Optional<User> user = userRepository.findById(100L);
        assertThat(user).isPresent();
        User foundUser = user.get();

        assertThat(foundUser.getLoginId()).isEqualTo("testUser");
        assertThat(foundUser.getEmail()).isEqualTo("testUser@example.com");
        assertThat(foundUser.getLastName()).isEqualTo("Doe");
        assertThat(foundUser.getFirstName()).isEqualTo("John");
        assertThat(foundUser.getNickname()).isEqualTo("tester");
        assertThat(foundUser.getProfileImagePath()).isEqualTo("/images/profile1.png");
        assertThat(foundUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(foundUser.getRole()).isEqualTo("USER");
        assertThat(foundUser.getCreatedDate()).isEqualTo("2024-12-01T08:00:00");
        assertThat(foundUser.getLastActivityDate()).isEqualTo("2024-12-18T10:00:00");
    }


    @Test
    void shouldFindByLoginIdSuccessfully_whenValidLoginIdProvided() {
        Optional<User> user = userRepository.findByUserLoginId("testUser");
        assertThat(user).isPresent();
        User foundUser = user.get();

        assertThat(foundUser.getLoginId()).isEqualTo("testUser");
        assertThat(foundUser.getEmail()).isEqualTo("testUser@example.com");
        assertThat(foundUser.getLastName()).isEqualTo("Doe");
        assertThat(foundUser.getFirstName()).isEqualTo("John");
        assertThat(foundUser.getNickname()).isEqualTo("tester");
        assertThat(foundUser.getProfileImagePath()).isEqualTo("/images/profile1.png");
        assertThat(foundUser.getStatus()).isEqualTo("ACTIVE");
        assertThat(foundUser.getRole()).isEqualTo("USER");
        assertThat(foundUser.getCreatedDate()).isEqualTo("2024-12-01T08:00:00");
        assertThat(foundUser.getLastActivityDate()).isEqualTo("2024-12-18T10:00:00");
    }

    @Test
    void shouldFindByEmailSuccessfully_whenValidEmailProvided() {
        Optional<User> user = userRepository.findByEmail("testUser@example.com");
        assertThat(user).isPresent();
        User foundUser = user.get();

        assertThat(foundUser.getEmail()).isEqualTo("testUser@example.com");
    }

    @Test
    void shouldSaveUser_WhenValidUserProvided() {
        User user = User.builder()
                .loginId("testUser2")
                .email("testUser2@example.com")
                .password("password123!")
                .lastName("Doe")
                .firstName("John")
                .nickname("Test")
                .status(UserStatus.ACTIVE.name())
                .role(UserRole.USER.name())
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId(), "ID should not be null after saving the user");
        assertEquals("testUser2", savedUser.getLoginId(), "Login ID should be saved correctly");
        assertEquals("testUser2@example.com", savedUser.getEmail(), "Email should be saved correctly");
        assertEquals("Doe", savedUser.getLastName(), "Last Name should be saved correctly");
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Test", savedUser.getNickname());
        assertNotNull(savedUser.getCreatedDate(), "Created Date should be set correctly");

        assertTrue(savedUser.getCreatedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(savedUser.getLastLoginDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(savedUser.getPasswordLastModifiedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(savedUser.getLastActivityDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldUpdateUser_WhenValidUserProvided() {
        User updatingUser = User.builder()
                .password("newPassword")
                .nickname("newNickname")
                .profileImagePath("/newImagePath")
                .build();

        User updatedUser = userRepository.update(updatingUser);

        // Assert
        assertEquals("newPassword", updatedUser.getPassword());
        assertEquals("/newImagePath", updatedUser.getProfileImagePath());
        assertEquals("newNickname", updatedUser.getNickname());
    }

    @Test
    void shouldReturnTrue_WhenEmailExists() {
        String email = "testUser@example.com";

        boolean exists = userRepository.emailExists(email);

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalse_WhenEmailDoesNotExist() {
        String email = "nonexistent@example.com";

        boolean exists = userRepository.emailExists(email);

        assertFalse(exists);
    }

    @Test
    void shouldReturnTrue_WhenLoginIdExists() {
        String loginId = "testUser";

        boolean exists = userRepository.userLoginIdExists(loginId);

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalse_WhenLoginIdDoesNotExist() {
        String loginId = "nonexistentLoginId";

        boolean exists = userRepository.userLoginIdExists(loginId);

        assertFalse(exists);
    }

    @Test
    void testFindAllByLastActivityDateBefore() {
        LocalDateTime thresholdDate = LocalDateTime.now().plusDays(60);

        List<User> result = userRepository.findAllByLastActivityDateBefore(thresholdDate);

        // 결과 검증
        assert(result.size() == 1);
        assert(result.get(0).getId() == 1L);
    }

    @Test
    void testUpdateAll() {
        User user1 = User.builder().status("INACTIVE").id(1L).build();
        User user2 = User.builder().status("INACTIVE").id(100L).build();
        int[] result = userRepository.updateAll(Arrays.asList(user1, user2));

        assert(result.length == 2);
    }

}