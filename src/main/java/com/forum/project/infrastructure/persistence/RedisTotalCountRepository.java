package com.forum.project.infrastructure.persistence;

import com.forum.project.domain.question.TotalCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Profile("redis")
public class RedisTotalCountRepository implements TotalCountRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String TOTAL_COUNT_KEY = "question:totalCount";

    @Override
    public void setTotalCount(Long count) {
        redisTemplate.opsForValue().set(TOTAL_COUNT_KEY, count.toString(), 3600, TimeUnit.SECONDS);
    }

    @Override
    public Long getTotalCount() {
        String value = redisTemplate.opsForValue().get(TOTAL_COUNT_KEY);
        return value != null ? Long.parseLong(value) : null;
    }

    @Override
    public void incrementTotalCount() {
        redisTemplate.opsForValue().increment(TOTAL_COUNT_KEY, 1);
    }

    @Override
    public void decrementTotalCount() {
        redisTemplate.opsForValue().decrement(TOTAL_COUNT_KEY, 1);
    }

}
