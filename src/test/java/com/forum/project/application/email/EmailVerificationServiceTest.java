package com.forum.project.application.email;

import com.forum.project.infrastructure.email.EmailSender;
import com.forum.project.common.utils.RandomStringGenerator;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.email.EmailVerification;
import com.forum.project.infrastructure.redis.VerificationCodeStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailSender emailSender;
    @Mock
    private VerificationCodeStore verificationCodeStore;
    @Mock
    private RandomStringGenerator randomStringGenerator;
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private final String toEmail = "receiver@example.com";

    @Test
    void shouldSendVerificationCodeSuccessfully() {
        String code = "123456";
        String subject = "Verification Email";
        String body = "<h1>Hello!</h1><p>Verification Code: " + code + "</p>";
        EmailVerification emailVerification = new EmailVerification(code, false);

        when(randomStringGenerator.generate(6)).thenReturn(code);
        doNothing().when(verificationCodeStore).saveVerificationCode(toEmail, emailVerification, 3);
        doNothing().when(emailSender).sendEmail(toEmail, subject, body);

        emailVerificationService.sendVerificationCode(toEmail);

        verify(randomStringGenerator).generate(6);
        verify(verificationCodeStore).saveVerificationCode(toEmail, emailVerification, 3);
        verify(emailSender).sendEmail(toEmail, subject, body);
    }

    @Test
    void shouldVerifyCodeSuccessfully() {
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, false);

        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(emailVerification);
        doNothing().when(verificationCodeStore).updateVerificationCode(toEmail, emailVerification, 10);

        emailVerificationService.verifyCode(toEmail, code);

        verify(verificationCodeStore).getVerificationCode(toEmail);
        verify(verificationCodeStore)
                .updateVerificationCode(toEmail, emailVerification, 10L);

        assertTrue(emailVerification.isVerified());
    }

    @Test
    void shouldFailVerifyingCode_WhenEmailVerificationIsNull() {
        String code = "123456";
        EmailVerification emailVerification = null;

        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyCode(toEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getVerificationCode(toEmail);
    }

    @Test
    void shouldFailVerifyingCode_whenCodeIsNotEqualToInputCode() {
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification("invalid", false);

        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyCode(toEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getVerificationCode(toEmail);
    }

    @Test
    void shouldVerifyEmailSuccessfully() {
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, true);

        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(emailVerification);

        assertDoesNotThrow(() -> emailVerificationService.verifyEmail(toEmail));

        assertTrue(emailVerification.isVerified());
        verify(verificationCodeStore).getVerificationCode(toEmail);
    }

    @Test
    void shouldFailVerifyEmail_whenEmailVerificationIsNull() {
        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(null);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyEmail(toEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getVerificationCode(toEmail);
    }

    @Test
    void shouldFailVerifyEmail_whenVerifiedFalse() {
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, false);
        when(verificationCodeStore.getVerificationCode(toEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyEmail(toEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getVerificationCode(toEmail);
    }
}