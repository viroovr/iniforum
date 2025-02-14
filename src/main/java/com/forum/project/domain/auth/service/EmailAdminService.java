package com.forum.project.domain.auth.service;

import com.forum.project.core.config.AppProperties;
import com.forum.project.infrastructure.jwt.EmailSender;
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
