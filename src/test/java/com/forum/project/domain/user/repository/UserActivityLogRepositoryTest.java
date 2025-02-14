package com.forum.project.domain.user.repository;

import com.forum.project.domain.user.vo.UserAction;
import com.forum.project.domain.user.entity.UserActivityLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataElasticsearchTest
@Testcontainers
@EnableElasticsearchRepositories(basePackages = "com.forum.project.domain.repository")
@Slf4j
class UserActivityLogRepositoryTest {

    private static final DockerImageName ELASTIC_IMAGE =
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.5");
    @Container
    private static final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false");

    @Autowired
    private UserActivityLogRepository userActivityLogRepository;

    @BeforeAll
    static void startContainer() {
        container.start();
//        String elasticsearchUrl = "http://" + container.getHost() + ":" + container.getMappedPort(9200);
//        System.setProperty("spring.elasticsearch.uris", elasticsearchUrl);
        System.setProperty("spring.elasticsearch.urls", container.getHttpHostAddress());
    }

    @AfterAll
    static void stopContainer() {
        container.stop();
    }

    @Test
    void testDatabaseIsRunning() {
        assertThat(container.isRunning()).isTrue();
    }

    @Test
    void saveUserActivityLog_success() {
        UserActivityLog log = UserActivityLog.builder()
                .id("log123")
                .userId(1L)
                .timestamp(Instant.now())
                .action(UserAction.LOGIN_SUCCESS.name())
                .build();

        UserActivityLog savedLog = userActivityLogRepository.save(log);

        assertThat(savedLog).isNotNull();
        assertThat(savedLog.getId()).isEqualTo("log123");
    }
}
