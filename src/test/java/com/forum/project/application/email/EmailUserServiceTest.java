package com.forum.project.application.email;

import com.forum.project.domain.user.User;
import com.forum.project.infrastructure.config.AppProperties;
import com.forum.project.infrastructure.email.EmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailUserServiceTest {
    @Mock
    private EmailSender emailSender;
    @Mock
    private AppProperties appProperties;
    @InjectMocks
    private EmailUserService emailUserService;

    @Test
    void testSendProfileUpdateEmail_success() {
        User user = User.builder().email("valid@email.com").build();

        doNothing().when(emailSender).sendEmail(eq(user.getEmail()), anyString(), anyString());
        emailUserService.sendProfileUpdateEmail(user);

        verify(emailSender).sendEmail(eq(user.getEmail()), anyString(), anyString());
    }

    @Test
    void testSendPasswordResetEmail_success() {
        String toEmail = "valid@email.com";
        String resetToken = "validResetToken";

        when(appProperties.getUrl()).thenReturn("appUrl");
        doNothing().when(emailSender).sendEmail(eq(toEmail), anyString(), anyString());

        emailUserService.sendPasswordResetEmail(toEmail, resetToken);

        verify(appProperties).getUrl();
        verify(emailSender).sendEmail(eq(toEmail), anyString(), anyString());
    }
}