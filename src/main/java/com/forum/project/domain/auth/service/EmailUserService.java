package com.forum.project.domain.auth.service;

import com.forum.project.domain.user.entity.User;
import com.forum.project.core.config.AppProperties;
import com.forum.project.infrastructure.jwt.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailUserService {
    private final EmailSender emailSender;
    private final AppProperties appProperties;

    public void sendProfileUpdateEmail(User user) {
        String userEmail = user.getEmail();
        String subject = "Profile Updated";
        String body = "Your profile has been successfully updated.";
        emailSender.sendEmail(userEmail, subject, body);
    }

    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        String resetLink = appProperties.getUrl() + "/reset-password?token=" + resetToken;
        String subject = "비밀번호 재설정 요청";
        String body = "<p>안녕하세요,</p>" +
                "<p>아래 링크를 클릭하여 비밀번호를 재설정하세요:</p>" +
                "<a href=\"" + resetLink + "\">비밀번호 재설정 링크</a>" +
                "<p>해당 링크는 일정 시간 후 만료됩니다.</p>" +
                "<p>감사합니다.</p>";

        emailSender.sendEmail(userEmail, subject, body);
    }
}
