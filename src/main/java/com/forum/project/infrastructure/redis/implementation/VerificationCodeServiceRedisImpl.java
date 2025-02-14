package com.forum.project.infrastructure.redis.implementation;

import com.forum.project.domain.auth.entity.EmailVerification;
import com.forum.project.domain.auth.repository.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceRedisImpl implements VerificationCodeService {
    private final RedisTemplate<String, Object> redisTemplateObject;
    private static final String REDIS_PREFIX = "verified:email:";

    private String generateKey(String key) {
        return REDIS_PREFIX + key;
    }

    /**
     * @param duration TimeUnit.MINUTES
     */
    public void save(String key, EmailVerification verification, long duration) {
        redisTemplateObject.opsForValue().set(generateKey(key), verification, duration, TimeUnit.MINUTES);
    }

    public EmailVerification get(String key) {
        return (EmailVerification) redisTemplateObject.opsForValue().get(generateKey(key));
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplateObject.delete(generateKey(key)));
    }
}
