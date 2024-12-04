package com.forum.project.application.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("dev")
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private RedisServer redisServer;

    @BeforeEach
    void setup() {
        if (redisServer != null && !redisServer.isActive()) {
            redisServer.start();
        }
    }

    @AfterEach
    void tearDown() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    @Test
    void testEmbeddedRedis() {
        // Set a key-value pair
        redisTemplate.opsForValue().set("testKey", "testValue");

        // Retrieve the value
        String value = (String) redisTemplate.opsForValue().get("testKey");

        // Assert the value
        assertThat(value).isEqualTo("testValue");
    }
}