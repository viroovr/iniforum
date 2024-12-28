package com.forum.project.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
public class RedisConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private RedisConnectionFactory connectionFactory;

    @BeforeEach
    public void setUp() {
        connectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
    }

    @AfterEach
    public void tearDown() {
        // Redis에서 테스트용 데이터 삭제
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().close();
    }

    @Test
    public void testDevProfile_RedisEmbeddedServer() {
        // Given: dev 프로파일에서 Redis 설정
        assertThat(connectionFactory).isNotNull();
        assertThat(redisTemplate).isNotNull();

        // When: RedisTemplate을 통해 데이터 저장
        String testKey = "dev:test";
        String testValue = "Embedded Redis Test";
        redisTemplate.opsForValue().set(testKey, testValue);

        // Then: Redis에 값이 정상적으로 저장되었는지 확인
        String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
        assertThat(retrievedValue).isEqualTo(testValue);
    }
}

@SpringBootTest
@ActiveProfiles("prod") // 테스트 환경: prod 프로파일
class RedisConfigProdTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private RedisConnectionFactory connectionFactory;

    @BeforeEach
    public void setUp() {
        connectionFactory = applicationContext.getBean(RedisConnectionFactory.class);
    }

    @AfterEach
    public void tearDown() {
        // Redis에서 테스트용 데이터 삭제
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().close();
    }

    @Test
    public void testProdProfile_RedisDockerServer() {
        // Given: prod 프로파일에서 Docker Redis 설정
        assertThat(connectionFactory).isNotNull();
        assertThat(redisTemplate).isNotNull();

        // When: RedisTemplate을 통해 데이터 저장
        String testKey = "prod:test";
        String testValue = "Docker Redis Test";
        redisTemplate.opsForValue().set(testKey, testValue);

        // Then: Redis에 값이 정상적으로 저장되었는지 확인
        String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
        assertThat(retrievedValue).isEqualTo(testValue);
    }
}