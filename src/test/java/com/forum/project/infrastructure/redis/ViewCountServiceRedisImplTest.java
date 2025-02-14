package com.forum.project.infrastructure.redis;

import com.forum.project.domain.question.entity.ViewCount;
import com.forum.project.infrastructure.redis.implementation.ViewCountServiceRedisImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.forum.project.infrastructure.redis.implementation")
@Slf4j
@ExtendWith(RedisTestContainerConfig.class)
class ViewCountServiceRedisImplTest {

    @Autowired
    private ViewCountServiceRedisImpl viewCountServiceRedisImpl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplateObject;

    private static final String REDIS_PREFIX = "question:viewCount:";
    private static final String KEY = "question1";

    @AfterEach
    void cleanUp() {
        redisTemplateObject.delete(Objects.requireNonNull(redisTemplateObject.keys(REDIS_PREFIX + "*")));
    }

    @Test
    void setViewCount() {

        viewCountServiceRedisImpl.setViewCount(KEY, 10L);
        ViewCount result = (ViewCount) redisTemplateObject.opsForValue().get(REDIS_PREFIX + KEY);

        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(KEY);
        assertThat(result.getCount()).isEqualTo((Long) 10L);
    }

    @Test
    void getViewCount() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);

        ViewCount result = viewCountServiceRedisImpl.getViewCount(KEY);

        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(KEY);
        assertThat(result.getCount()).isEqualTo((Long) 10L);
    }

    @Test
    void increment() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);

        viewCountServiceRedisImpl.increment(KEY);

        ViewCount result = (ViewCount) redisTemplateObject.opsForValue().get(REDIS_PREFIX + KEY);
        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(KEY);
        assertThat(result.getCount()).isEqualTo(10L + 1);
    }

    @Test
    void decrement() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);

        viewCountServiceRedisImpl.decrement(KEY);

        ViewCount result = (ViewCount) redisTemplateObject.opsForValue().get(REDIS_PREFIX + KEY);
        assertThat(result).isNotNull();
        assertThat(result.getKey()).isEqualTo(KEY);
        assertThat(result.getCount()).isEqualTo(10L - 1);
    }

    @Test
    void hasKey() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);

        boolean result = viewCountServiceRedisImpl.hasKey(KEY);

        assertThat(result).isTrue();
    }

    @Test
    void hasKey_notExists() {
        boolean result = viewCountServiceRedisImpl.hasKey(KEY);

        assertThat(result).isFalse();
    }

    @Test
    void getAllKeys() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);
        viewCountServiceRedisImpl.setViewCount(KEY + "1", 10L);

        Set<String> result = viewCountServiceRedisImpl.getAllKeys();

        assertThat(result).containsExactlyInAnyOrderElementsOf(
                Set.of(REDIS_PREFIX + KEY, REDIS_PREFIX + KEY + "1")
        );
    }

    @Test
    void clearAllKeys() {
        viewCountServiceRedisImpl.setViewCount(KEY, 10L);
        viewCountServiceRedisImpl.setViewCount(KEY + "1", 10L);

        viewCountServiceRedisImpl.clearAllKeys();

        Set<String> result = viewCountServiceRedisImpl.getAllKeys();
        assertThat(result).isEmpty();
    }
}