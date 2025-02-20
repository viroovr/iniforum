package com.forum.project.domain.email.service;

import com.forum.project.core.config.AppProperties;
import com.forum.project.domain.email.util.EmailTemplateProvider;
import com.forum.project.infrastructure.jwt.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class EmailServiceTest {
    @InjectMocks
    private EmailService emailService;

    @Mock private EmailSender emailSender;
    @Mock private AppProperties appProperties;
    @Mock private EmailTemplateProvider emailTemplateProvider;

    private static final String VERIFICATION_SUBJECT = "이메일 인증 코드";
    private static final String PASSWORD_RESET_SUBJECT = "비밀번호 재설정 요청";

    private String toEmail;

    @BeforeEach
    void setUp() {
        toEmail = "user@example.com";
    }

    @Test
    void sendEmailToAdmin() {
        when(appProperties.getAdminEmail()).thenReturn("admin@example.com");

        emailService.sendEmailToAdmin("subject", "body");

        verify(emailSender).sendEmail("admin@example.com", "subject", "body");
    }

    @Test
    void sendVerificationEmail() {
        when(emailTemplateProvider.getVerificationEmail("123456")).thenReturn("body");

        emailService.sendVerificationEmail(toEmail, "123456");

        verify(emailSender).sendEmail(toEmail, VERIFICATION_SUBJECT, "body");
    }

    private String createResetLink() {
        when(appProperties.getUrl()).thenReturn("https://test.com");
        return "https://test.com" + "/reset-password?token=" + "resetToken";
    }

    @Test
    void sendPasswordResetEmail() {
        when(emailTemplateProvider.getPasswordResetEmail(createResetLink())).thenReturn("body");

        emailService.sendPasswordResetEmail(toEmail, "resetToken");

        verify(appProperties).getUrl();
        verify(emailSender).sendEmail(toEmail, PASSWORD_RESET_SUBJECT, "body");
    }
}