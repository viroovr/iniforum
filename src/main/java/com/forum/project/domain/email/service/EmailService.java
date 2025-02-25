package com.forum.project.domain.email.service;

import com.forum.project.core.config.AppProperties;
import com.forum.project.domain.email.util.EmailTemplateProvider;
import com.forum.project.infrastructure.jwt.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;
    private final AppProperties appProperties;
    private final EmailTemplateProvider emailTemplateProvider;

    private static final String VERIFICATION_SUBJECT = "이메일 인증 코드";
    private static final String PASSWORD_RESET_SUBJECT = "비밀번호 재설정 요청";

    public void sendEmail(String recipient, String subject, String body) {
        emailSender.sendEmail(recipient, subject, body);
    }

    public void sendEmailToAdmin(String subject, String body) {
        sendEmail(appProperties.getAdminEmail(), subject, body);
    }

    public void sendVerificationEmail(String toEmail, String code) {
        sendEmail(toEmail, VERIFICATION_SUBJECT, emailTemplateProvider.getVerificationEmail(code));
    }

    private String createResetLink(String resetToken) {
        return String.format("%s/reset-password?token=%s", appProperties.getUrl(), resetToken);
    }

    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        sendEmail(userEmail, PASSWORD_RESET_SUBJECT, emailTemplateProvider.getPasswordResetEmail(createResetLink(resetToken)));
    }
}
