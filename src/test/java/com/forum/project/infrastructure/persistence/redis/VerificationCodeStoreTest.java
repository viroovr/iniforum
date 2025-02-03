package com.forum.project.infrastructure.persistence.redis;

import com.forum.project.domain.email.EmailVerification;
import com.forum.project.infrastructure.persistence.email.VerificationCodeStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@ActiveProfiles("test")
class VerificationCodeStoreTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private VerificationCodeStore verificationCodeStore;

    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:latest").withExposedPorts(6379);

    @BeforeAll
    static void setUpRedis() {
        redisContainer.start();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
    }

    private static final String EMAIL = "test@example.com";
    private static final String CODE = "123456";

    @Test
    void testSaveAndGetValue() {
        // Given
        String email = "test@example.com";
        EmailVerification verification = new EmailVerification("123456", false);

        // When
        verificationCodeStore.save(email, verification, 5);
        EmailVerification retrieved = verificationCodeStore.getValue(email);

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getVerificationCode()).isEqualTo("123456");
    }


    @Test
    void shouldSaveSuccessfully_whenValidArgumentProvided() {
        EmailVerification verification = new EmailVerification(CODE, false);

        verificationCodeStore.save(EMAIL, verification, 3);

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
        verificationCodeStore.save(EMAIL, verification, 3);

        // When
        EmailVerification retrievedVerification = verificationCodeStore.getValue(EMAIL);

        // Then
        assertNotNull(retrievedVerification);
        assertEquals(CODE, retrievedVerification.getVerificationCode());
        assertFalse(retrievedVerification.isVerified());
    }

    @Test
    void shouldUpdateSuccessfully() {
        EmailVerification verification = new EmailVerification(CODE, false);
        verificationCodeStore.save(EMAIL, verification, 3);

        verification.setVerified(true);
        verificationCodeStore.update(EMAIL, verification, 5);

        EmailVerification updatedVerification =
                (EmailVerification) redisTemplate.opsForValue().get("verified:email:" + EMAIL);
        assertNotNull(updatedVerification);
        assertTrue(updatedVerification.isVerified());
    }

}