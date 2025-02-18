package com.forum.project.infrastructure.redis;

import com.forum.project.infrastructure.redis.implementation.TokenBlacklistHandlerRedisImpl;
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
class TokenBlacklistHandlerRedisImplTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplateObject;

    @Autowired
    private TokenBlacklistHandlerRedisImpl tokenBlacklistHandlerRedis;

    private final String REDIS_PREFIX_REFRESH_TOKEN = "blacklist:refreshToken:";
    private final String REDIS_PREFIX_ACCESS_TOKEN = "blacklist:accessToken:";

    @AfterEach
    void cleanUp() {
        redisTemplateObject.delete(Objects.requireNonNull(redisTemplateObject.keys(REDIS_PREFIX_ACCESS_TOKEN + "*")));
        redisTemplateObject.delete(Objects.requireNonNull(redisTemplateObject.keys(REDIS_PREFIX_REFRESH_TOKEN + "*")));
    }

    @Test
    void blacklistAccessTokenAndGet() {
        String accessToken = "valid-access-token";

        tokenBlacklistHandlerRedis.blacklistAccessToken(accessToken, 3600L);

        boolean result = tokenBlacklistHandlerRedis.isBlacklistedAccessToken(accessToken);
        assertThat(result).isTrue();
    }

    @Test
    void blacklistRefreshTokenAndGet() {
        String refreshToken = "valid-refresh-token";

        tokenBlacklistHandlerRedis.blacklistRefreshToken(refreshToken, 3600L);

        boolean result = tokenBlacklistHandlerRedis.isBlacklistedRefreshToken(refreshToken);
        assertThat(result).isTrue();
    }
}