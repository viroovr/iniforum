package com.forum.project.infrastructure.redis;

import com.forum.project.domain.email.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VerificationCodeStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_PREFIX = "verified:email:";

    public void saveVerificationCode(String email, EmailVerification verification, long duration) {
        redisTemplate.opsForValue().set(REDIS_PREFIX + email, verification, duration, TimeUnit.MINUTES);
    }

    public EmailVerification getVerificationCode(String email) {
        return (EmailVerification) redisTemplate.opsForValue().get(REDIS_PREFIX + email);
    }

    public void updateVerificationCode(String email, EmailVerification verification, long duration) {
        saveVerificationCode(email, verification, duration);
    }
}
