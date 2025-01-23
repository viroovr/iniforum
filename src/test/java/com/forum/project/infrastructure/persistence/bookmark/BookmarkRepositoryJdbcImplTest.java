package com.forum.project.infrastructure.persistence.bookmark;

import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@JdbcTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BookmarkRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Clock clock;
    private BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        bookmarkRepository = new BookmarkRepositoryJdbcImpl(jdbcTemplate, clock);

        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE bookmarks (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "notes VARCHAR(255), " +
                "created_date TIMESTAMP, " +
                "last_accessed_date TIMESTAMP " +
                ");");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS bookmarks;");
    }

    @Test
    void findByUserIdAndQuestionId() {
        insertTestData(1L, 1L, "testNotes");

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndQuestionId(1L, 1L);
        assertThat(bookmark).isPresent();

        Bookmark savedBookmark = bookmark.get();
        assertThat(savedBookmark).isNotNull();
        assertThat(savedBookmark.getQuestionId()).isEqualTo(1L);
        assertThat(savedBookmark.getUserId()).isEqualTo(1L);
        assertThat(savedBookmark.getNotes()).isEqualTo("testNotes");
        assertThat(savedBookmark.getCreatedDate()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test
    void insert() {
        Bookmark bookmark = createBookmark(1L, 1L);

        Bookmark savedBookmark = bookmarkRepository.insert(bookmark);
        assertThat(savedBookmark).isNotNull();
        assertThat(savedBookmark.getQuestionId()).isEqualTo(1L);
        assertThat(savedBookmark.getUserId()).isEqualTo(1L);
        assertThat(savedBookmark.getNotes()).isEqualTo("testNotes");
        assertThat(savedBookmark.getCreatedDate()).isEqualTo(LocalDateTime.now(clock));
        assertThat(savedBookmark.getLastAccessedDate()).isNull();
    }

    @Test
    void delete() {
        Long userId = 1L;
        Long questionId = 1L;
        insertTestData(userId, questionId, "testNotes");

        bookmarkRepository.delete(userId, questionId);

        String sql = BookmarkQueries.findByQuestionIdAndUserId();
        SqlParameterSource source = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);

        List<Bookmark> result = jdbcTemplate.query(sql, source, new BeanPropertyRowMapper<>(Bookmark.class));

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void findAllByUserId() {
        Long userId = 1L;
        insertTestData(userId, 2L, "testNotes1");
        insertTestData(userId, 3L, "testNotes2");
        insertTestData(2L, 4L, "notFind");

        List<Bookmark> result = bookmarkRepository.findAllByUserId(userId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
            .extracting("userId", "questionId", "notes")
            .containsExactlyInAnyOrder(
                tuple(userId, 2L, "testNotes1"),
                tuple(userId, 3L, "testNotes2")
            );
    }

    @Test
    void existsByUserIdAndQuestionId() {
        insertTestData(1L, 1L, "testNotes1");

        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndQuestionId_NotExists() {
        insertTestData(1L, 2L, "testNotes2");

        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isFalse();
    }

    private Bookmark createBookmark(Long userId, Long questionId) {
        return Bookmark.builder()
                .userId(userId)
                .questionId(questionId)
                .notes("testNotes")
                .build();
    }

    private void insertTestData(Long userId, Long questionId, String notes) {
        String sql = BookmarkQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId)
                .addValue("notes", notes)
                .addValue("createdDate", LocalDateTime.now(clock))
                .addValue("lastAccessedDate", LocalDateTime.now(clock));
        jdbcTemplate.update(sql, params);
    }
}