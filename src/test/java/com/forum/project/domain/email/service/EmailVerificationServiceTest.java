package com.forum.project.domain.email.service;

import com.forum.project.core.common.RandomStringGenerator;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.entity.EmailVerification;
import com.forum.project.domain.auth.repository.VerificationCodeService;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private EmailService emailService;
    @Mock
    private VerificationCodeService verificationCodeService;
    @Mock
    private RandomStringGenerator randomStringGenerator;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_TIME = 3;
    private static final int REGISTER_EXPIRATION_TIME = 10;

    private static final String EMAIL = "valid@email.com";
    private static final String CODE = "123456";

    private EmailVerification emailVerification;

    @BeforeEach
    void setUp() {
        emailVerification = new EmailVerification(CODE, false);
    }

    @Test
    void sendVerificationCode() {
        when(randomStringGenerator.generate(CODE_LENGTH)).thenReturn(CODE);

        emailVerificationService.sendVerificationCode(EMAIL);

        verify(randomStringGenerator).generate(CODE_LENGTH);
        verify(verificationCodeService).save(EMAIL, emailVerification, CODE_EXPIRATION_TIME);
        verify(emailService).sendVerificationEmail(EMAIL, CODE);
    }

    @Test
    void verifyEmailCode() {
        when(verificationCodeService.get(EMAIL)).thenReturn(emailVerification);

        emailVerificationService.verifyEmailCode(EMAIL, CODE);

        assertThat(emailVerification).returns(true, EmailVerification::isVerified);
        verify(verificationCodeService).get(EMAIL);
        verify(verificationCodeService).save(EMAIL, emailVerification, REGISTER_EXPIRATION_TIME);
    }

    @Test
    void verifyEmailCode_notExists() {
        when(verificationCodeService.get(EMAIL)).thenReturn(null);

        TestUtils.assertApplicationException(
                () -> emailVerificationService.verifyEmailCode(EMAIL, CODE),
                ErrorCode.INVALID_VERIFICATION_CODE
        );
    }

    @Test
    void verifyEmailCode_notCodeMatch() {
        when(verificationCodeService.get(EMAIL)).thenReturn(
                new EmailVerification("differentCode", true));

        TestUtils.assertApplicationException(
                () -> emailVerificationService.verifyEmailCode(EMAIL, CODE),
                ErrorCode.INVALID_VERIFICATION_CODE
        );
    }

    @Test
    void validateEmailCode() {
        when(verificationCodeService.get(EMAIL)).thenReturn(new EmailVerification(CODE, true));

        assertThatCode(() -> emailVerificationService.validateEmailCode(EMAIL)).doesNotThrowAnyException();

        verify(verificationCodeService).get(EMAIL);
    }

    @Test
    void validateEmailCode_notExists() {
        when(verificationCodeService.get(EMAIL)).thenReturn(null);

        TestUtils.assertApplicationException(
                () -> emailVerificationService.validateEmailCode(EMAIL),
                ErrorCode.INVALID_VERIFICATION_CODE
        );
    }

    @Test
    void validateEmailCode_notVerified() {
        when(verificationCodeService.get(EMAIL)).thenReturn(emailVerification);

        TestUtils.assertApplicationException(
                () -> emailVerificationService.validateEmailCode(EMAIL),
                ErrorCode.INVALID_VERIFICATION_CODE
        );
    }
}