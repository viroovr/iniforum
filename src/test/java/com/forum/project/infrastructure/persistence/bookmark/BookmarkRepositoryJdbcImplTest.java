package com.forum.project.infrastructure.persistence.bookmark;

import com.forum.project.common.utils.DateUtil;
import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkKey;
import com.forum.project.domain.bookmark.BookmarkRepository;
import lombok.extern.slf4j.Slf4j;
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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@JdbcTest
@ExtendWith(MockitoExtension.class)
@Slf4j
class BookmarkRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        bookmarkRepository = new BookmarkRepositoryJdbcImpl(jdbcTemplate);

        jdbcTemplate.getJdbcTemplate().execute("CREATE TABLE bookmarks (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "notes VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_accessed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP " +
                ");");
    }

    private Bookmark createBookmark(Long userId, Long questionId) {
        return Bookmark.builder()
                .userId(userId)
                .questionId(questionId)
                .notes("testNotes")
                .build();
    }

    private void insertData(Long userId, Long questionId, String notes) {
        String sql = BookmarkQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId)
                .addValue("notes", notes);
        jdbcTemplate.update(sql, params);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS bookmarks;");
    }

    @Test
    void findByUserIdAndQuestionId() {
        insertData(1L, 1L, "testNotes");

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndQuestionId(1L, 1L);
        assertThat(bookmark).isPresent();

        Bookmark savedBookmark = bookmark.get();
        assertThat(savedBookmark).isNotNull();
        assertThat(savedBookmark.getQuestionId()).isEqualTo(1L);
        assertThat(savedBookmark.getUserId()).isEqualTo(1L);
        assertThat(savedBookmark.getNotes()).isEqualTo("testNotes");
        log.info(savedBookmark.toString());
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Bookmark bookmark = createBookmark(1L, 1L);
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = bookmarkRepository.insertAndReturnGeneratedKeys(bookmark);

        assertThat(generatedKeys).isNotNull();
        assertThat(generatedKeys.size()).isEqualTo(BookmarkKey.getKeys().length);

        Long generatedId = (Long) generatedKeys.get(BookmarkKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(BookmarkKey.CREATED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();
        assertThat(DateUtil.timeDifferenceWithinLimit(expectedTimestamp, createdDate)).isTrue();
    }

    @Test
    void delete() {
        Long userId = 1L;
        Long questionId = 1L;
        insertData(userId, questionId, "testNotes");

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
        insertData(userId, 2L, "testNotes1");
        insertData(userId, 3L, "testNotes2");
        insertData(2L, 4L, "notFind");

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
        insertData(1L, 1L, "testNotes1");

        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndQuestionId_NotExists() {
        insertData(1L, 2L, "testNotes2");

        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isFalse();
    }
}