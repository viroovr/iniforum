package com.forum.project.infrastructure.redis;

import com.forum.project.domain.email.EmailVerification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VerificationCodeStoreTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private VerificationCodeStore verificationCodeStore;

    private static final String EMAIL = "test@example.com";
    private static final String CODE = "123456";

    @Test
    void shouldSaveVerificationCodeSuccessfully_whenValidArgumentProvided() {
        EmailVerification verification = new EmailVerification(CODE, false);

        verificationCodeStore.saveVerificationCode(EMAIL, verification, 3);

        EmailVerification storedVerification =
                (EmailVerification) redisTemplate.opsForValue().get("verified:email:" + EMAIL);
        assertNotNull(storedVerification);
        assertEquals(CODE, storedVerification.getVerificationCode());
        assertFalse(storedVerification.isVerified());
    }

    @Test
    void shouldReturnVerificationCode_whenValidEmailProvided() {
        // Given
        EmailVerification verification = new EmailVerification(CODE, false);
        verificationCodeStore.saveVerificationCode(EMAIL, verification, 3);

        // When
        EmailVerification retrievedVerification = verificationCodeStore.getVerificationCode(EMAIL);

        // Then
        assertNotNull(retrievedVerification);
        assertEquals(CODE, retrievedVerification.getVerificationCode());
        assertFalse(retrievedVerification.isVerified());
    }

    @Test
    void shouldUpdateVerificationCodeSuccessfully() {
        EmailVerification verification = new EmailVerification(CODE, false);
        verificationCodeStore.saveVerificationCode(EMAIL, verification, 3);

        verification.setVerified(true);
        verificationCodeStore.updateVerificationCode(EMAIL, verification, 5);

        EmailVerification updatedVerification =
                (EmailVerification) redisTemplate.opsForValue().get("verified:email:" + EMAIL);
        assertNotNull(updatedVerification);
        assertTrue(updatedVerification.isVerified());
    }

}