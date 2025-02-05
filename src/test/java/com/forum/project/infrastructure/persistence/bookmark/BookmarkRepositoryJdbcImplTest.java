package com.forum.project.infrastructure.persistence.bookmark;

import com.forum.project.common.utils.DateUtils;
import com.forum.project.domain.bookmark.Bookmark;
import com.forum.project.domain.bookmark.BookmarkKey;
import com.forum.project.domain.bookmark.BookmarkRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
@JdbcTest
@ActiveProfiles("test")
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
        log.info("Test Data Inserted userId:{}, questionId:{}, notes:{}", userId, questionId, notes);
    }

    private Optional<Bookmark> findData(Long id) {
        String sql = BookmarkQueries.findById();
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            Bookmark result = jdbcTemplate.queryForObject(sql, params,
                    new BeanPropertyRowMapper<>(Bookmark.class));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS bookmarks;");
    }

    @Test
    void findByUserIdAndQuestionId() {
        insertData(1L, 1L, "testNotes");

        Optional<Bookmark> bookmark = bookmarkRepository.findByUserIdAndQuestionId(1L, 1L);
        assertThat(bookmark).isNotEmpty();

        Bookmark savedBookmark = bookmark.get();

        assertThat(savedBookmark)
                .extracting(Bookmark::getQuestionId, Bookmark::getUserId, Bookmark::getNotes)
                .containsExactly(1L, 1L, "testNotes");

        log.info(savedBookmark.toString());
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Bookmark bookmark = createBookmark(1L, 1L);
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, Object> generatedKeys = bookmarkRepository.insertAndReturnGeneratedKeys(bookmark);

        assertThat(generatedKeys).isNotNull();

        Long generatedId = (Long) generatedKeys.get(BookmarkKey.ID);
        Timestamp createdDate = (Timestamp) generatedKeys.get(BookmarkKey.CREATED_DATE);

        assertThat(generatedId).isEqualTo(1L);
        assertThat(createdDate).isNotNull();

        assertThat(DateUtils.timeDifferenceWithinLimit(expectedTimestamp, createdDate)).isTrue();
    }

    @Test
    void delete() {
        insertData(1L, 1L, "testNotes");
        assertThat(findData(1L)).isPresent();

        bookmarkRepository.delete(1L, 1L);

        assertThat(findData(1L)).isEmpty();
        assertThatCode(() -> bookmarkRepository.delete(999L, 999L)).doesNotThrowAnyException();
    }

    @Test
    void findAllByUserId() {
        Long userId = 1L;
        insertData(userId, 2L, "testNotes1");
        insertData(userId, 3L, "testNotes2");
        insertData(2L, 4L, "notFind");

        List<Bookmark> result = bookmarkRepository.findAllByUserId(userId);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .allSatisfy(bookmark ->
                        assertThat(bookmark.getUserId()).isOne());
    }

    @Test
    void existsByUserIdAndQuestionId() {
        insertData(1L, 1L, "testNotes1");

        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndQuestionId_NotExists() {
        boolean result = bookmarkRepository.existsByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isFalse();
    }
}