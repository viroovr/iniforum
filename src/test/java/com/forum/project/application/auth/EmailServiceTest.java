package com.forum.project.application.auth;

import com.forum.project.application.security.RandomStringGenerator;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.dtos.EmailVerification;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "spring.mail.username=test@example.com")
class EmailServiceTest {

    @Mock
    private RedisTemplate<String, EmailVerification> redisTemplate;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private final String REDIS_PREFIX = "verified:";
    private final String code = "123456";
    private final String fromEmail = "sender@example.com";
    private final String toEmail = "receiver@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@example.com");
    }

    @Test
    @DisplayName("sendVerificationCode - Redis 및 JavaMailSender를 올바르게 호출하는지 테스트")
    void testSendVerificationCode() {
        EmailVerification emailVerification = new EmailVerification(code, false);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        try (MockedStatic<RandomStringGenerator> mockedStatic = mockStatic(RandomStringGenerator.class)) {
            mockedStatic.when(() -> RandomStringGenerator.generateRandomString(any(Integer.class))).thenReturn(code);

            MimeMessage mimeMessage = new MimeMessage((Session) null);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(redisTemplate.opsForValue()).thenReturn(valueOps);
            doNothing().when(javaMailSender).send(mimeMessage);

            emailService.sendVerificationCode(toEmail);

            mockedStatic.verify(() -> RandomStringGenerator.generateRandomString(6));
            verify(redisTemplate.opsForValue(), times(1))
                    .set(eq(REDIS_PREFIX + toEmail), eq(emailVerification), eq(3L), eq(TimeUnit.MINUTES));
            verify(javaMailSender, times(1)).send(mimeMessage);
        }
    }

    @Test
    void testStoreVerificationCode() {
        EmailVerification emailVerification = new EmailVerification(code, false);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(REDIS_PREFIX + fromEmail, emailVerification, 3, TimeUnit.MINUTES);

        emailService.storeVerificationCode(fromEmail, code);

        verify(redisTemplate).opsForValue();
        verify(valueOps, times(1))
                .set(eq(REDIS_PREFIX + fromEmail), eq(emailVerification), eq(3L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testVerifyCode_Success() {
        EmailVerification emailVerification = new EmailVerification(code, false);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doNothing().when(valueOps).set(REDIS_PREFIX + fromEmail, emailVerification, 10, TimeUnit.MINUTES);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        emailService.verifyCode(fromEmail, code);


        verify(redisTemplate, times(2)).opsForValue();
        verify(valueOps).get(REDIS_PREFIX + fromEmail);
        verify(valueOps)
                .set(eq(REDIS_PREFIX + fromEmail), eq(emailVerification), eq(10L), eq(TimeUnit.MINUTES));

        assertTrue(emailVerification.isVerified());
        assertNotNull(valueOps.get(REDIS_PREFIX + fromEmail));
        assertTrue(valueOps.get(REDIS_PREFIX + fromEmail).isVerified());
        assertEquals(emailVerification.getVerificationCode(), code);
        assertEquals(emailVerification.getVerificationCode(), code);
    }

    @Test
    void testVerifyCode_nullEmailVerification_InvalidVerificationCode() {
        EmailVerification emailVerification = null;
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailService.verifyCode(fromEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(redisTemplate, times(1)).opsForValue();
        verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
    }

    @Test
    void testVerifyCode_notValidCode_InvalidVerificationCode() {
        EmailVerification emailVerification = new EmailVerification("invalid", false);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailService.verifyCode(fromEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(redisTemplate, times(1)).opsForValue();
        verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
    }

    @Test
    void testVerifyEmail_Success() {
        EmailVerification emailVerification = new EmailVerification(code, true);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        assertDoesNotThrow(() -> emailService.verifyEmail(fromEmail));

        assertTrue(emailVerification.isVerified());
        verify(redisTemplate, times(1)).opsForValue();
        verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
    }

    @Test
    void testVerifyEmail_nullEmailVerification_InvalidVerificationCode() {
        EmailVerification emailVerification = null;
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailService.verifyEmail(fromEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(redisTemplate, times(1)).opsForValue();
        verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
    }

    @Test
    void testVerifyEmail_notVerified_InvalidVerificationCode() {
        EmailVerification emailVerification = new EmailVerification(code, false);
        ValueOperations<String, EmailVerification> valueOps = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(REDIS_PREFIX + fromEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailService.verifyEmail(fromEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(redisTemplate, times(1)).opsForValue();
        verify(redisTemplate.opsForValue()).get(REDIS_PREFIX + fromEmail);
    }
}