package com.forum.project.application.email;

import com.forum.project.infrastructure.config.AppProperties;
import com.forum.project.infrastructure.email.EmailSender;
import com.forum.project.common.utils.RandomStringGenerator;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.email.EmailVerification;
import com.forum.project.infrastructure.persistence.email.VerificationCodeStore;
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
    @Mock
    private AppProperties appProperties;
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Test
    void testSendVerificationCode_success() {
        String toEmail = "valid@email.com";
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, false);

        when(randomStringGenerator.generate(6)).thenReturn(code);
        doNothing().when(verificationCodeStore).save(toEmail, emailVerification, 3);
        doNothing().when(emailSender).sendEmail(eq(toEmail), anyString(), anyString());

        emailVerificationService.sendVerificationCode(toEmail);

        verify(randomStringGenerator).generate(6);
        verify(verificationCodeStore).save(toEmail, emailVerification, 3);
        verify(emailSender).sendEmail(eq(toEmail), anyString(), anyString());
    }

    @Test
    void testVerifyEmailCode_success() {
        String toEmail = "valid@email.com";
        String code = "valid.";
        EmailVerification verification = new EmailVerification(code, false);

        when(verificationCodeStore.getValue(toEmail)).thenReturn(verification);
        doNothing().when(verificationCodeStore).update(toEmail, verification, 10);

        emailVerificationService.verifyEmailCode(toEmail, code);

        assertTrue(verification.isVerified());
        verify(verificationCodeStore).getValue(toEmail);
        verify(verificationCodeStore).update(toEmail, verification, 10L);
    }

    @Test
    void testVerifyEmailCode_NullInput_ThrowsInvalidCode() {
        String toEmail = "valid@email.com";
        String code = "123456";

        when(verificationCodeStore.getValue(toEmail)).thenReturn(null);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyEmailCode(toEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
    }

    @Test
    void testVerifyEmailCode_DifferCode_ThrowsInvalidCode() {
        String toEmail = "valid@email.com";
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification("invalid", false);

        when(verificationCodeStore.getValue(toEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.verifyEmailCode(toEmail, code));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
    }

    @Test
    void testValidateEmailCode_success() {
        String toEmail = "valid@email.com";
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, true);

        when(verificationCodeStore.getValue(toEmail)).thenReturn(emailVerification);

        assertDoesNotThrow(() -> emailVerificationService.validateEmailCode(toEmail));

        assertTrue(emailVerification.isVerified());
        verify(verificationCodeStore).getValue(toEmail);
    }

    @Test
    void testValidateEmailCode_NullInput_ThrowsInvalidCode() {
        String toEmail = "valid@email.com";
        when(verificationCodeStore.getValue(toEmail)).thenReturn(null);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.validateEmailCode(toEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getValue(toEmail);
    }

    @Test
    void testValidateEmailCode_NotVerified_ThrowsInvalidCode() {
        String toEmail = "valid@email.com";
        String code = "123456";
        EmailVerification emailVerification = new EmailVerification(code, false);
        when(verificationCodeStore.getValue(toEmail)).thenReturn(emailVerification);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> emailVerificationService.validateEmailCode(toEmail));

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, applicationException.getErrorCode());
        verify(verificationCodeStore).getValue(toEmail);
    }
}