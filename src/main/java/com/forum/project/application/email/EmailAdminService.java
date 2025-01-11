package com.forum.project.application.email;

import com.forum.project.infrastructure.config.AppProperties;
import com.forum.project.infrastructure.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAdminService {
    private final EmailSender emailSender;
    private final AppProperties appProperties;

    public void sendEmail(String subject, String body) {
        emailSender.sendEmail(appProperties.getAdminEmail(), subject, body);
    }
}
