package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.domain.bookmark.dto.BookmarkRequestDto;
import com.forum.project.domain.bookmark.entity.Bookmark;
import com.forum.project.domain.bookmark.repository.BookmarkRepository;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.domain.bookmark.vo.BookmarkKey;
import com.forum.project.infrastructure.persistence.queries.BookmarkQueries;
import com.forum.project.presentation.dtos.TestDtoFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
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

    private BookmarkRequestDto bookmarkRequestDto;
    @BeforeEach
    void setUp() {
        bookmarkRequestDto = TestDtoFactory.createBookmarkRequestDto();
        bookmarkRepository = new BookmarkRepositoryJdbcImpl(jdbcTemplate);

        JdbcTestUtils.dropTable(jdbcTemplate, "bookmarks");
        JdbcTestUtils.createTable(jdbcTemplate, "bookmarks",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "question_id BIGINT NOT NULL, " +
                "notes VARCHAR(255), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_accessed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
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
        return JdbcTestUtils.findData(jdbcTemplate, BookmarkQueries.findById(), id, Bookmark.class);
    }

    @Test
    void findByUserIdAndQuestionId() {
        insertData(1L, 1L, "testNotes");

        Optional<Bookmark> result = bookmarkRepository.findByUserIdAndQuestionId(1L, 1L);
        assertThat(result).isNotEmpty()
                .hasValueSatisfying(bookmark -> {
                    assertThat(bookmark.getQuestionId()).isOne();
                    assertThat(bookmark.getUserId()).isOne();
                });
    }

    @Test
    void findByUserIdAndQuestionId_notFound() {
        Optional<Bookmark> result = bookmarkRepository.findByUserIdAndQuestionId(1L, 1L);

        assertThat(result).isEmpty();
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        LocalDateTime now = ClockUtil.now();
        Optional<BookmarkKey> result = bookmarkRepository.insertAndReturnGeneratedKeys(bookmarkRequestDto);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(bookmarkKey -> {
                    assertThat(bookmarkKey.getId()).isOne();
                    assertThat(bookmarkKey.getCreatedDate()).isAfterOrEqualTo(now);
                });
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

        List<Bookmark> result = bookmarkRepository.findAllByUserId(userId, PageRequest.of(0, 10));

        assertThat(result)
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