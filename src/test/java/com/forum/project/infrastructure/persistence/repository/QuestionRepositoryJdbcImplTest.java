package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.question.vo.QuestionSortType;
import com.forum.project.domain.question.entity.Question;
import com.forum.project.domain.question.repository.QuestionRepository;
import com.forum.project.domain.question.vo.QuestionStatus;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import com.forum.project.infrastructure.persistence.key.QuestionKey;
import com.forum.project.infrastructure.persistence.queries.QuestionQueries;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@JdbcTest
@ActiveProfiles("test")
@Slf4j
class QuestionRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        questionRepository = new QuestionRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "questions");
        JdbcTestUtils.createTable(jdbcTemplate, "questions",
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "status ENUM('OPEN', 'CLOSED', 'RESOLVED', 'DELETED') DEFAULT 'OPEN', " +
                        "view_count BIGINT DEFAULT 0 CHECK (view_count >= 0), " +
                        "up_voted_count BIGINT DEFAULT 0 CHECK (up_voted_count >= 0), " +
                        "down_voted_count BIGINT DEFAULT 0 CHECK (down_voted_count >= 0), " +
                        "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
    }

    private Question createQuestion(Long userId, String status) {
        return Question.builder()
                .userId(userId)
                .title("testTitle")
                .content("testContent")
                .status(status)
                .build();
    }

    private void insertTestData(Long userId, String status) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("title", "testTitle")
                .addValue("content", "testContent")
                .addValue("status", status);
        jdbcTemplate.update(QuestionQueries.insertAndReturnGeneratedKeys(), params);
    }

    private void insertTestData(Long userId, String title, String content) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("title", title)
                .addValue("content", content)
                .addValue("status", QuestionStatus.OPEN.name());
        jdbcTemplate.update(QuestionQueries.insertAndReturnGeneratedKeys(), params);
    }

    private void insertTestData(Long userId, Long viewCount, Long upVotedCount, Long downVotedCount) {
        String sql = "INSERT INTO questions " +
                "(user_id, title, content, view_count, up_voted_count, down_voted_count)" +
                "VALUES (:userId, :title, :content, :viewCount, :upVotedCount, :downVotedCount)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("title", "testTitle")
                .addValue("content", "testContent")
                .addValue("viewCount", viewCount)
                .addValue("upVotedCount", upVotedCount)
                .addValue("downVotedCount", downVotedCount);
        jdbcTemplate.update(sql, params);
    }

    private Optional<Question> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, QuestionQueries.findById(), id, Question.class);
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Timestamp expectedTimestamp = Timestamp.valueOf(LocalDateTime.now());
        Map<String, Object> generatedKeys = questionRepository.insertAndReturnGeneratedKeys(
                createQuestion(1L, QuestionStatus.OPEN.name()));

        assertThat(generatedKeys).hasSize(QuestionKey.getKeys().length);

        assertThat((Long) generatedKeys.get(QuestionKey.ID)).isOne();
        assertThat((Timestamp) generatedKeys.get(QuestionKey.CREATED_DATE)).isAfter(expectedTimestamp);
        assertThat((Timestamp) generatedKeys.get(QuestionKey.LAST_MODIFIED_DATE)).isAfter(expectedTimestamp);
    }

    @Test
    void findById() {
        insertTestData(1L, QuestionStatus.OPEN.name());

        Optional<Question> result = questionRepository.findById(1L);

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(question -> {
                    assertThat(question.getId()).isOne();
                    assertThat(question.getUserId()).isOne();
                    assertThat(question.getStatus()).isEqualTo(QuestionStatus.OPEN.name());
                    assertThat(question.getUpVotedCount()).isZero();
                });

        result.ifPresent(question -> log.info(question.toString()));
    }

    @Test
    void findById_notExists() {
        Optional<Question> result = questionRepository.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findQuestionByIds() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(2L, QuestionStatus.OPEN.name());
        insertTestData(3L, QuestionStatus.OPEN.name());
        insertTestData(3L, QuestionStatus.OPEN.name());

        List<Question> questions = questionRepository.findQuestionByIds(List.of(2L, 3L));

        assertThat(questions)
                .hasSize(2)
                .extracting(Question::getId)
                .containsExactlyInAnyOrder(2L, 3L);

        questions.forEach(question -> log.info(question.toString()));
    }

    @Test
    void deleteById() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        assertThat(findData(1L)).isPresent();

        questionRepository.deleteById(1L);

        assertThat(findData(1L)).isEmpty();
        assertThatCode(() -> questionRepository.deleteById(999L)).doesNotThrowAnyException();
    }

    @Test
    void existsById() {
        insertTestData(1L, QuestionStatus.OPEN.name());

        boolean result = questionRepository.existsById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_notExists() {
        boolean result = questionRepository.existsById(1L);

        assertThat(result).isFalse();
    }

    @Test
    void findByUserId() {
        insertTestData(1L, QuestionStatus.CLOSED.name());
        insertTestData(1L, QuestionStatus.CLOSED.name());
        insertTestData(2L, QuestionStatus.OPEN.name());

        List<Question> result = questionRepository.findByUserId(1L, 0, 10);

        assertThat(result)
                .hasSize(2)
                .allSatisfy(question ->assertThat(question.getUserId()).isOne());
    }

    @Test
    void findByStatus() {
        insertTestData(1L, QuestionStatus.CLOSED.name());
        insertTestData(1L, QuestionStatus.CLOSED.name());
        insertTestData(2L, QuestionStatus.OPEN.name());
        insertTestData(3L, QuestionStatus.CLOSED.name());

        List<Question> result = questionRepository.findByStatus(QuestionStatus.CLOSED.name(), 0, 10);

        assertThat(result)
                .hasSize(3)
                .allSatisfy(question ->
                        assertThat(question.getStatus()).isEqualTo(QuestionStatus.CLOSED.name()));

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void searchByTitle() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "1keywordTitle1", "keywordContent1");
        insertTestData(1L, "2keywordTitle2", "keywordContent2");
        insertTestData(1L, "KeywordTitle3", "keywordContent3");
        insertTestData(1L, "Title3", "keywordContent3");
        insertTestData(1L, "keyTitle3", "keywordContent3");

        List<Question> result = questionRepository.searchByTitle("keyword", 0, 10);

        assertThat(result)
                .hasSize(4)
                .allSatisfy(question ->
                        assertThat(question.getTitle().toLowerCase()).contains("keyword"));

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void searchByContent() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "keywordTitle2", "keywordContent1");
        insertTestData(1L, "keywordTitle3", "2keywordContent2");
        insertTestData(1L, "keywordTitle4", "KeywordContent3");
        insertTestData(1L, "keywordTitle5", "KeyContent4");
        insertTestData(1L, "keywordTitle6", "Content5");

        List<Question> result = questionRepository.searchByContent("keyword", 0, 10);

        assertThat(result)
                .hasSize(4)
                .allSatisfy(question ->
                        assertThat(question.getTitle()).containsIgnoringCase("keyword"));

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void searchByTitleOrContent() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "Title2", "keywordContent1");
        insertTestData(1L, "keywordTitle3", "2Content2");
        insertTestData(1L, "Title4", "KeywordContent4");
        insertTestData(1L, "keyWORDTitle5", "content5");
        insertTestData(1L, "Title6", "Content6");

        List<Question> result = questionRepository.searchByTitleOrContent("keyword", 0, 10);

        assertThat(result)
                .hasSize(5)
                .allSatisfy(question ->
                            assertThat(
                                    question.getTitle().toLowerCase().contains("keyword") ||
                                    question.getContent().toLowerCase().contains("keyword"))
                                    .isTrue()
                );

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void updateViewCount() {
        insertTestData(1L, 10L, 0L, 0L);

        int result = questionRepository.updateViewCount(1L, 5L);

        assertThat(result).isOne();
        assertThat(findData(1L))
                .isNotEmpty()
                .hasValueSatisfying(question ->
                        assertThat(question.getViewCount()).isEqualTo(15L)
                );
    }

    @Test
    void updateTitleAndContent() {
        insertTestData(1L, "testTitle", "testContent");

        int result = questionRepository.updateTitleAndContent(1L, "newTitle", "newContent");

        assertThat(result).isOne();

        assertThat(findData(1L))
                .isNotEmpty()
                .hasValueSatisfying(question -> {
                    assertThat(question.getTitle()).isEqualTo("newTitle");
                    assertThat(question.getContent()).isEqualTo("newContent");
                });
    }

    @Test
    void updateUpVotedCount_increment() {
        insertTestData(1L, 0L, 10L, 10L);

        int result = questionRepository.updateUpVotedCount(1L, 1L);

        assertThat(result).isOne();
        assertThat(findData(1L))
                .isNotEmpty()
                .hasValueSatisfying(question ->
                    assertThat(question.getUpVotedCount()).isEqualTo(11L)
                );
    }

    @Test
    void updateUpVotedCount_decrement() {
        insertTestData(1L, 0L, 10L, 10L);

        int result = questionRepository.updateUpVotedCount(1L, -1L);

        assertThat(result).isOne();
        assertThat(findData(1L)).isNotEmpty()
                .hasValueSatisfying(question ->
                    assertThat(question.getUpVotedCount()).isEqualTo(9L)
                );
    }

    @Test
    void updateDownVotedCount_increment() {
        insertTestData(1L, 0L, 10L, 8L);

        int result = questionRepository.updateDownVotedCount(1L, 1L);

        assertThat(result).isOne();
        assertThat(findData(1L)).isNotEmpty()
                .hasValueSatisfying(question ->
                    assertThat(question.getDownVotedCount()).isEqualTo(9L)
                );
    }

    @Test
    void updateDownVotedCount_decrement() {
        insertTestData(1L, 0L, 10L, 8L);

        int result = questionRepository.updateDownVotedCount(1L, -2L);

        assertThat(result).isOne();
        assertThat(findData(1L)).isNotEmpty()
                .hasValueSatisfying(question ->
                    assertThat(question.getDownVotedCount()).isEqualTo(6L)
                );
    }

    @Test
    void getByPage_limit() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(2L, QuestionStatus.OPEN.name());
        insertTestData(3L, QuestionStatus.OPEN.name());

        List<Question> result = questionRepository.getByPage(0, 2);

        assertThat(result).hasSize(2);
    }

    @Test
    void getByPage_offset() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(2L, QuestionStatus.OPEN.name());
        insertTestData(3L, QuestionStatus.OPEN.name());

        List<Question> result = questionRepository.getByPage(1, 2);

        assertThat(result).hasSize(1);
    }

    @Test
    void getByPageable_upVotesDesc() {
        Pageable pageable = PageRequest.of(0, 10, QuestionSortType.UP_VOTES_DESC.getSort());

        insertTestData(1L, 1L, 10L, 0L);
        insertTestData(1L, 1L, 5L, 0L);
        insertTestData(1L, 1L, 15L, 0L);

        List<Question> result = questionRepository.getByPageable(pageable);

        assertThat(result)
                .hasSize(3)
                .extracting(Question::getUpVotedCount)
                .isSortedAccordingTo((a, b) -> Long.compare(b, a));

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void getByPageable_downVotesAsc() {
        Pageable pageable = PageRequest.of(0, 10, QuestionSortType.DOWN_VOTES_ASC.getSort());

        insertTestData(1L, 1L, 10L, 15L);
        insertTestData(1L, 1L, 5L, 5L);
        insertTestData(1L, 1L, 15L, 10L);

        List<Question> result = questionRepository.getByPageable(pageable);

        assertThat(result)
                .hasSize(3)
                .extracting(Question::getDownVotedCount)
                .isSorted();

        result.forEach(question -> log.info(question.toString()));
    }

    @Test
    void getViewCountById() {
        insertTestData(1L, 100L, 0L, 0L);

        Long result = questionRepository.getViewCountById(1L);

        assertThat(result).isEqualTo(100L);
    }

    @Test
    void countAll() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(1L, QuestionStatus.OPEN.name());

        Long result = questionRepository.countAll();

        assertThat(result).isEqualTo(3L);
    }

    @Test
    void countByStatus() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(1L, QuestionStatus.DELETED.name());
        insertTestData(1L, QuestionStatus.RESOLVED.name());

        Long result = questionRepository.countByStatus(QuestionStatus.RESOLVED.name());

        assertThat(result).isOne();
    }

    @Test
    void countByTitleKeyword() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "1keywordTitle1", "keywordContent1");
        insertTestData(1L, "2keywordTitle2", "keywordContent2");
        insertTestData(1L, "KeywordTitle3", "keywordContent3");
        insertTestData(1L, "Title3", "keywordContent3");
        insertTestData(1L, "keyTitle3", "keywordContent3");

        Long result = questionRepository.countByTitleKeyword("keyword");

        assertThat(result).isEqualTo(4L);
    }

    @Test
    void countByContentKeyword() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "keywordTitle2", "keywordContent1");
        insertTestData(1L, "keywordTitle3", "2keywordContent2");
        insertTestData(1L, "keywordTitle4", "KeywordContent3");
        insertTestData(1L, "keywordTitle5", "KeyContent4");
        insertTestData(1L, "keywordTitle6", "Content5");

        Long result = questionRepository.countByContentKeyword("keyword");

        assertThat(result).isEqualTo(4L);
    }

    @Test
    void countByContentOrTitleKeyword() {
        insertTestData(1L, "keywordTitle", "keywordContent");
        insertTestData(1L, "Title2", "keywordContent1");
        insertTestData(1L, "keywordTitle3", "2Content2");
        insertTestData(1L, "Title4", "KeywordContent4");
        insertTestData(1L, "keyWORDTitle5", "content5");
        insertTestData(1L, "Title6", "Content6");

        Long result = questionRepository.countByContentOrTitleKeyword("keyword");

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void countByUserId() {
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(2L, QuestionStatus.OPEN.name());

        Long result = questionRepository.countByUserId(1L);

        assertThat(result).isEqualTo(2L);
    }

    @Test
    void countByQuestionIds() {
        insertTestData(1L, QuestionStatus.DELETED.name());
        insertTestData(1L, QuestionStatus.OPEN.name());
        insertTestData(2L, QuestionStatus.OPEN.name());

        Long result = questionRepository.countByQuestionIds(List.of(2L, 3L));

        assertThat(result).isEqualTo(2L);
    }
}