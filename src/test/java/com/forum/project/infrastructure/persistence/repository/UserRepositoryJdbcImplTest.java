package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.user.dto.UserCreateDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.vo.UserKey;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.user.vo.UserStatus;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.infrastructure.persistence.queries.UserQueries;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
@JdbcTest
@ActiveProfiles("test")
class UserRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "users");
        JdbcTestUtils.createTable(jdbcTemplate, "users",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "login_id VARCHAR(255) NOT NULL UNIQUE, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "last_name VARCHAR(255) NOT NULL, " +
                "first_name VARCHAR(255) NOT NULL, " +
                "nickname VARCHAR(255), " +
                "profile_image_path VARCHAR(255), " +
                "status VARCHAR(50) NOT NULL, " +
                "role VARCHAR(50) NOT NULL, " +
                "last_activity_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_password_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
    }

    private UserCreateDto createUserCreateDto(String loginId, String email) {
        return UserCreateDto.builder()
                .loginId(loginId)
                .email(email)
                .password("testPassword")
                .lastName("testLastName")
                .firstName("testFirstName")
                .profileImagePath("test/path")
                .status(UserStatus.ACTIVE.name())
                .role(UserRole.USER.name())
                .build();
    }

    private User createUser(String password, String profileImagePath, String nickname, String status) {
        return User.builder()
                .id(1L)
                .loginId("testLoginId")
                .email("testEmail")
                .password(password)
                .lastName("testLastName")
                .firstName("testFirstName")
                .nickname(nickname)
                .profileImagePath(profileImagePath)
                .status(status)
                .role(UserRole.USER.name())
                .build();
    }

    private void insertData(String loginId, String email) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("loginId", loginId)
                .addValue("email", email)
                .addValue("password", "testPassword")
                .addValue("lastName", "testLastname")
                .addValue("firstName", "testFirstname")
                .addValue("nickname", "testNickname")
                .addValue("profileImagePath", "test/path")
                .addValue("status", UserStatus.ACTIVE.name())
                .addValue("role", UserRole.USER.name());
        jdbcTemplate.update(UserQueries.insertAndReturnGeneratedKeys(), params);
    }

    private Optional<User> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, UserQueries.findById(), id, User.class);
    }

    @Test
    void findById() {
        insertData("testLoginId", "test@email.com");

        Optional<User> result = userRepository.findById(1L);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isOne();
                    assertThat(user.getLoginId()).isEqualTo("testLoginId");
                    assertThat(user.getEmail()).isEqualTo("test@email.com");
                });
    }

    @Test
    void findByLoginId() {
        insertData("testLoginId", "test@email.com");

        Optional<User> result = userRepository.findByLoginId("testLoginId");

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(user ->
                        assertThat(user.getLoginId()).isEqualTo("testLoginId")
                );
    }

    @Test
    void findByLoginId_notExists() {
        Optional<User> result = userRepository.findByLoginId("testLoginId");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail() {
        insertData("testLoginId", "test@email.com");

        Optional<User> result = userRepository.findByEmail("test@email.com");

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(user ->
                    assertThat(user.getEmail()).isEqualTo("test@email.com")
                );
    }

    @Test
    void findByEmail_notExists() {
        Optional<User> result = userRepository.findByEmail("test@email.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByLastActivityDateBefore() {
        insertData("testLoginId", "test@email.com");
        insertData("testLoginId1", "test1@email.com");
        LocalDateTime thresholdDate = LocalDateTime.now().plusDays(1);

        List<User> result = userRepository.findAllByLastActivityDateBefore(thresholdDate);

        assertThat(result)
                .hasSize(2)
                .allSatisfy(user -> assertThat(user.getLastActivityDate()).isBefore(thresholdDate));

        result.forEach(user -> log.info(user.toString()));
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        LocalDateTime beforeNow = LocalDateTime.now();

        Optional<UserKey> result = userRepository.insertAndReturnGeneratedKeys(
                createUserCreateDto("testLoginId", "test@email.com"));

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(key -> {
                    assertThat(key.getId()).isOne();
                    assertThat(key.getCreatedDate()).isAfter(beforeNow);
                    assertThat(key.getLastActivityDate()).isAfter(beforeNow);
                    assertThat(key.getLastLoginDate()).isAfter(beforeNow);
                    assertThat(key.getLastPasswordModifiedDate()).isAfter(beforeNow);
                });
    }

    @Test
    void updateProfile() {
        insertData("testLoginId", "test@email.com");

        int result = userRepository.updateProfile(
                createUser("newPassword", "newImage/path",
                "newNickname", UserStatus.SUSPENDED.name()));

        assertThat(result).isOne();
        assertThat(findData(1L))
                .isNotEmpty()
                .hasValueSatisfying(aUser -> {
                    assertThat(aUser.getPassword()).isEqualTo("newPassword");
                    assertThat(aUser.getProfileImagePath()).isEqualTo("newImage/path");
                    assertThat(aUser.getNickname()).isEqualTo("newNickname");
                    assertThat(aUser.getStatus()).isEqualTo(UserStatus.SUSPENDED.name());
                });
    }

    @Test
    void updateAllStatus() {
        insertData("testLoginId", "test@email.com");
        insertData("testLoginId1", "test1@email.com");
        List<Long> userIds = List.of(1L, 2L);
        List<String> statuses = List.of(UserStatus.INACTIVE.name(), UserStatus.DELETED.name());

        int result = userRepository.updateAllStatus(userIds, statuses);

        assertThat(result).isEqualTo(2);

        IntStream.range(0, 2).forEach(i -> {
            assertThat(findData(userIds.get(i)))
                    .isNotEmpty()
                    .hasValueSatisfying(user ->
                            assertThat(user.getStatus()).isEqualTo(statuses.get(i))
                    );
        });
    }

    @Test
    void searchByLoginIdAndStatus() {
        insertData("keywordLoginId", "test@email.com");
        insertData("3KEYWordLoginId", "test1@email.com");

        List<User> result = userRepository.searchByLoginIdAndStatus("keyword", UserStatus.ACTIVE.name()
                ,0, 10);

        assertThat(result)
                .hasSize(2)
                .allSatisfy(user -> {
                    assertThat(user.getLoginId()).containsIgnoringCase("keyword");
                    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE.name());
                });
    }

    @Test
    void countByLoginIdAndStatus() {
        insertData("keywordLoginId", "test@email.com");
        insertData("3KEYWordLoginId", "test1@email.com");

        Long result = userRepository.countByLoginIdAndStatus("keyword", UserStatus.ACTIVE.name());

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void existsByEmail() {
        insertData("loginId", "test@email.com");

        boolean result = userRepository.existsByEmail("test@email.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_notExists() {
        boolean result = userRepository.existsByEmail("test@email.com");

        assertThat(result).isFalse();
    }

    @Test
    void existsByLoginId() {
        insertData("loginId", "test@email.com");

        boolean result = userRepository.existsByLoginId("loginId");

        assertThat(result).isTrue();
    }

    @Test
    void existsByLoginId_notExists() {
        boolean result = userRepository.existsByLoginId("loginId");

        assertThat(result).isFalse();
    }

    @Test
    void existsById() {
        insertData("loginId", "test@email.com");

        boolean result = userRepository.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_notExists() {
        boolean result = userRepository.existsById(1L);

        assertThat(result).isFalse();
    }

    @Test
    void getLoginIdById() {
        insertData("testLoginId", "testEmail@email.com");

        String result = userRepository.getLoginIdById(1L);

        assertThat(result).isEqualTo("testLoginId");
    }

    @Test
    void delete() {
        insertData("testLoginId", "testEmail@email.com");
        assertThat(findData(1L)).isPresent();

        userRepository.delete(1L);

        assertThat(findData(1L)).isEmpty();
        assertThatCode(() -> userRepository.delete(999L)).doesNotThrowAnyException();
    }
}