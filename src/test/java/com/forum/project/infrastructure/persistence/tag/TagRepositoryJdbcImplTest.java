package com.forum.project.infrastructure.persistence.tag;

import com.forum.project.domain.tag.Tag;
import com.forum.project.domain.tag.TagKey;
import com.forum.project.domain.tag.TagRepository;
import com.forum.project.infrastructure.persistence.JdbcTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@ActiveProfiles("test")
@Slf4j
class TagRepositoryJdbcImplTest {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository = new TagRepositoryJdbcImpl(jdbcTemplate);
        JdbcTestUtils.dropTable(jdbcTemplate, "tags");
        JdbcTestUtils.createTable(jdbcTemplate,
                "tags", "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "usage_count BIGINT DEFAULT 0 CHECK (usage_count >= 0), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ");
    }

    private Tag createTag(String name) {
        return Tag.builder()
                .name(name)
                .build();
    }

    private void insertData(String name) {
        String sql = TagQueries.insert();
        SqlParameterSource params = new MapSqlParameterSource("name", name)
                .addValue("usageCount", 0);
        jdbcTemplate.update(sql, params);
    }

    private Optional<Tag> findData(Long id) {
        return JdbcTestUtils.findData(jdbcTemplate, TagQueries.findById(), id, Tag.class);
    }

    private void createTableOfQuestionTags() {
        JdbcTestUtils.createTable(jdbcTemplate,
                "question_tags",
                "question_id BIGINT NOT NULL," +
                "tag_id BIGINT NOT NULL," +
                "PRIMARY KEY (question_id, tag_id)");
    }

    private void insertQuestionTagData(Long questionId, Long tagId) {
        String insertQuestionTagSql = "INSERT INTO question_tags (question_id, tag_id) VALUES (:questionId, :tagId)";
        SqlParameterSource params = new MapSqlParameterSource("questionId", questionId)
                .addValue("tagId", tagId);
        jdbcTemplate.update(insertQuestionTagSql, params);
    }

    private void verifyTimestamps(Map<String, Object> result) {
        assertThat(((Timestamp) result.get(TagKey.CREATED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
        assertThat(((Timestamp) result.get(TagKey.LAST_MODIFIED_DATE)).toInstant())
                .isBeforeOrEqualTo(Timestamp.valueOf(LocalDateTime.now()).toInstant());
    }

    @Test
    void insertAndReturnGeneratedKeys() {
        Map<String, Object> result = tagRepository.insertAndReturnGeneratedKeys(createTag("testTag"));

        assertThat(result).hasSize(TagKey.getKeys().length);
        assertThat((Long) result.get(TagKey.ID)).isOne();
        verifyTimestamps(result);
    }

    @Test
    void saveAll() {
        Tag tag = createTag("testTag");
        Tag tag1 = createTag("testTag1");

        List<Map<String, Object>> result = tagRepository.saveAll(List.of(tag, tag1));

        assertThat(result)
                .hasSize(2)
                .extracting(map -> (Long) map.get(TagKey.ID))
                .containsExactlyInAnyOrder(1L, 2L);

        result.forEach(this::verifyTimestamps);
    }

    @Test
    void findById() {
        insertData("testName");

        Optional<Tag> result = tagRepository.findById(1L);

        assertThat(result).isNotEmpty()
                .hasValueSatisfying(tag -> assertThat(tag.getId()).isOne());
    }

    @Test
    void findByName() {
        insertData("testName");

        Optional<Tag> result = tagRepository.findByName("testName");

        assertThat(result).isNotEmpty()
                .hasValueSatisfying(tag ->
                        assertThat(tag.getName()).isEqualTo("testName")
                );
    }

    @Test
    void searchByName() {
        insertData("keywordName");
        insertData("keyword1Name1");
        insertData("KeyWordName");
        insertData("KeyName");

        List<Tag> result = tagRepository.searchByName("keyword", 0, 10);

        assertThat(result).hasSize(3)
                .allSatisfy(tag -> assertThat(tag.getName()).containsIgnoringCase("keyword"));
    }

    @Test
    void findByIds() {
        insertData("testName");
        insertData("testName1");
        insertData("testName2");

        List<Tag> result = tagRepository.findByIds(List.of(2L, 3L));

        assertThat(result).hasSize(2)
                .extracting(Tag::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void getByPage_limit() {
        insertData("testName");
        insertData("testName1");
        insertData("testName2");

        List<Tag> result = tagRepository.getByPage(0, 2);

        assertThat(result).hasSize(2);
    }

    @Test
    void getByPage_offset() {
        insertData("testName");
        insertData("testName1");
        insertData("testName2");

        List<Tag> result = tagRepository.getByPage(1, 2);

        assertThat(result).hasSize(1);
    }
    
    @Test
    void findTagsByQuestionId() {
        insertData("testName");
        insertData("testName1");

        createTableOfQuestionTags();
        insertQuestionTagData(1L, 1L);
        insertQuestionTagData(1L, 2L);

        List<Tag> result = tagRepository.findTagsByQuestionId(1L);

        assertThat(result).hasSize(2)
                .extracting(Tag::getId, Tag::getName)
                .containsExactlyInAnyOrder(
                        tuple(1L, "testName"),
                        tuple(2L, "testName1")
                );
    }

    @Test
    void findByNames() {
        insertData("testName");
        insertData("testName1");

        List<Tag> result = tagRepository.findByNames(List.of("testName", "testName1"));

        assertThat(result).hasSize(2)
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("testName", "testName1");
    }

    @Test
    void searchByNames() {
        insertData("firstName");
        insertData("1first1Name1");
        insertData("FirStName");
        insertData("SecondName");
        insertData("FirName");
        insertData("condName");

        List<Tag> result = tagRepository.searchByNames(List.of("first", "second"), 0, 10);

        assertThat(result).hasSize(4)
                .allSatisfy(tag ->
                    assertThat(tag.getName().toLowerCase().contains("first") ||
                            tag.getName().toLowerCase().contains("second"))
                            .isTrue()
                );

        result.forEach(tag -> log.info(tag.toString()));
    }

    @Test
    void existsByName() {
        insertData("testName");

        boolean result = tagRepository.existsByName("testName");

        assertThat(result).isTrue();
    }

    @Test
    void existsByName_notExists() {
        boolean result = tagRepository.existsByName("testName");

        assertThat(result).isFalse();
    }

    @Test
    void updateName() {
        insertData("testName");

        int result = tagRepository.updateName(1L, "newName");

        assertThat(result).isOne();
        assertThat(findData(1L)).hasValueSatisfying(tag -> {
            assertThat(tag.getName()).isEqualTo("newName");
        });
    }

    @Test
    void delete() {
        insertData("testName");
        assertThat(findData(1L)).isPresent();

        tagRepository.delete(1L);
        assertThat(findData(1L)).isEmpty();

        assertThatCode(() -> tagRepository.delete(999L)).doesNotThrowAnyException();
    }
}