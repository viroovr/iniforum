package com.forum.project.infrastructure;

import com.forum.project.domain.entity.Question;
import com.forum.project.domain.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@JdbcTest
@ContextConfiguration(classes = QuestionRepositoryImplTest.TestConfig.class)
@ActiveProfiles("test")
class QuestionRepositoryImplTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2024-01-01T10:15:30.00Z"), ZoneId.of("UTC"));
        }

        @Bean
        public QuestionRepository questionRepository(NamedParameterJdbcTemplate jdbcTemplate, Clock clock) {
            return new QuestionRepositoryImpl(jdbcTemplate, clock);
        }
    }

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private QuestionRepositoryImpl questionRepository;

    @BeforeEach
    void setUp() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        namedParameterJdbcTemplate.getJdbcTemplate().execute(
                """
                CREATE TABLE questions (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    user_id VARCHAR(255),
                    content TEXT,
                    tag VARCHAR(255),
                    created_date TIMESTAMP
                );
                """
        );
    }

    @Test
    void testSave_Success() {
        Question question = new Question(null, "testTitle", "testUser", "testContent", "testTag", null);
        Question savedQuestion = questionRepository.save(question);

        assertThat(savedQuestion.getId()).isEqualTo(1L);
        assertThat(savedQuestion.getCreatedDate()).isNotNull();
        assertThat(savedQuestion.getCreatedDate()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 15, 30));
        assertThat(savedQuestion.getTitle()).isEqualTo("testTitle");
        assertThat(savedQuestion.getUserId()).isEqualTo("testUser");
    }

    @Test
    void save_shouldThrowDataIntegrityViolationException_whenRequiredFieldIsMissing() {
        Question question = new Question(null, "testTitle", "testUser", "testContent", "testTag", null);

        assertThatThrownBy(() -> questionRepository.save(question))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_shouldHandleUnexpectedException() {
        Question question = null;

        assertThatThrownBy(() -> questionRepository.save(question))
                .isInstanceOf(RuntimeException.class);
    }

}