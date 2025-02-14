package com.forum.project.infrastructure.redis;

import com.forum.project.domain.auth.entity.EmailVerification;
import com.forum.project.infrastructure.redis.implementation.VerificationCodeServiceRedisImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@ComponentScan(basePackages = "com.forum.project.infrastructure.redis.implementation")
@Import(TestRedisConfiguration.class)
@ExtendWith(RedisTestContainerConfig.class)
@ActiveProfiles("test")
@Slf4j
class VerificationCodeServiceRedisImplTest {

    @Autowired
    private VerificationCodeServiceRedisImpl verificationCodeServiceRedisImpl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplateObject;

    private static final String REDIS_PREFIX = "verified:email:";

    @AfterEach
    void cleanUp() {
        redisTemplateObject.delete(Objects.requireNonNull(redisTemplateObject.keys(REDIS_PREFIX + "*")));
    }

    private EmailVerification createEmailVerification(String code) {
        return new EmailVerification(code, false);
    }

    @Test
    void saveAndGet() {
        String key = "test@example.com";

        verificationCodeServiceRedisImpl.save(key, createEmailVerification("123456"), 5);

        EmailVerification result = verificationCodeServiceRedisImpl.get(key);
        assertThat(result).isNotNull()
                .satisfies(v -> assertThat(v.getVerificationCode()).isEqualTo("123456"));
    }

    @Test
    void get_notExists() {
        String email = "nonexistent@example.com";

        EmailVerification retrieved = verificationCodeServiceRedisImpl.get(email);

        assertThat(retrieved).isNull();
    }

    @Test
    void delete() {
        String key = "test@example.com";
        verificationCodeServiceRedisImpl.save(key, createEmailVerification("123456"), 5);

        boolean result = verificationCodeServiceRedisImpl.delete(key);

        assertThat(result).isTrue();
    }

    @Test
    void delete_notExists() {
        String key = "test@example.com";

        boolean result = verificationCodeServiceRedisImpl.delete(key);

        assertThat(result).isFalse();
    }
}