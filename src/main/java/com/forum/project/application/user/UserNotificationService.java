package com.forum.project.application.user;

import com.forum.project.application.email.EmailService;
import com.forum.project.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final EmailService emailService;

    public void sendProfileUpdateNotification(User user) {
        emailService.sendNotification(
                user.getEmail(),
                "Profile Updated",
                "Your profile has been successfully updated.");
    }
}
