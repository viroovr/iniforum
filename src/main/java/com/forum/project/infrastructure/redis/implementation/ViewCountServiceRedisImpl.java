package com.forum.project.infrastructure.redis.implementation;

import com.forum.project.domain.question.entity.ViewCount;
import com.forum.project.domain.question.repository.ViewCountStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Profile("redis")
public class ViewCountServiceRedisImpl implements ViewCountStore {
    private final RedisTemplate<String, Object> redisTemplateObject;
    private static final String TOTAL_COUNT_KEY = "question:viewCount:";

    private String generateKey(String key) {
        return TOTAL_COUNT_KEY + key;
    }

    @Override
    public void setViewCount(String key, Long count) {
        ViewCount value = new ViewCount(key, count);
        redisTemplateObject.opsForValue().set(generateKey(key), value, 1, TimeUnit.HOURS);
    }

    @Override
    public ViewCount getViewCount(String key) {
        return (ViewCount) redisTemplateObject.opsForValue().get(generateKey(key));
    }

    private ViewCount getOrCreateViewCount(String key) {
        ViewCount viewCount = (ViewCount) redisTemplateObject.opsForValue().get(generateKey(key));

        if (viewCount == null) {
            viewCount = new ViewCount(key, 0L);
        }
        return viewCount;
    }

    @Override
    public void increment(String key) {
        ViewCount viewCount = getOrCreateViewCount(key);

        viewCount.increment();
        setViewCount(key, viewCount.getCount());
    }

    @Override
    public void decrement(String key) {
        ViewCount viewCount = getOrCreateViewCount(key);

        viewCount.decrement();
        setViewCount(key, viewCount.getCount());
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplateObject.hasKey(generateKey(key));
    }

    @Override
    public Set<String> getAllKeys() {
        return redisTemplateObject.keys(TOTAL_COUNT_KEY + "*");
    }

    @Override
    public void clearAllKeys() {
        Set<String> keys = redisTemplateObject.keys(TOTAL_COUNT_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplateObject.delete(keys);
        }
    }
}
