package com.forum.project.infrastructure.persistence;

import com.forum.project.domain.question.ViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Profile("redis")
public class ViewCountRedisRepository implements ViewCountRepository {

    private final RedisTemplate<String, Long> redisTemplate;
    private static final String TOTAL_COUNT_KEY = "question:viewCount:";

    private String generateKey(String key) {
        return TOTAL_COUNT_KEY + key;
    }

    @Override
    public void setViewCount(String key, Long count) {
        redisTemplate.opsForValue().set(generateKey(key), count, 1, TimeUnit.HOURS);
    }

    @Override
    public Long getViewCount(String key) {
        return redisTemplate.opsForValue().get(generateKey(key));
    }

    @Override
    public void increment(String key) {
        redisTemplate.opsForValue().increment(generateKey(key), 1);
    }

    @Override
    public void decrement(String key) {
        redisTemplate.opsForValue().decrement(generateKey(key), 1);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(generateKey(key));
    }

    @Override
    public Set<String> getAllKeys() {
        return redisTemplate.keys(TOTAL_COUNT_KEY + "*");
    }

    @Override
    public void clearAllKeys() {
        Set<String> keys = redisTemplate.keys(TOTAL_COUNT_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
