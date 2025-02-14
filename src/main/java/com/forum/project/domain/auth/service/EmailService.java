package com.forum.project.domain.auth.service;

import com.forum.project.infrastructure.jwt.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailSender emailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        String subject = "Verification Email";
        String body = String.format("<h1>Hello!</h1><p>Your verification code is: [%s]</p>", code);
        emailSender.sendEmail(toEmail, subject, body);
    }
}
