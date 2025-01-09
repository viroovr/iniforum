package com.forum.project.application.user.admin;

import com.forum.project.application.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminNotificationService {
    private final EmailService emailService;

    @Value("${admin.email}")
    private final String ADMIN_EMAIL;

    public void sendNotification(String subject, String body) {
        emailService.sendNotification(
                ADMIN_EMAIL,
                subject,
                body);
    }
}
