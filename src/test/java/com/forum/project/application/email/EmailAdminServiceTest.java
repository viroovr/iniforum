package com.forum.project.application.email;

import com.forum.project.infrastructure.config.AppProperties;
import com.forum.project.infrastructure.email.EmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailAdminServiceTest {
    @Mock
    private EmailSender emailSender;
    @Mock
    private AppProperties appProperties;
    @InjectMocks
    private EmailAdminService emailAdminService;

    @Test
    void testSendEmail_success() {
        String subject = "validSubject";
        String body = "validBody";
        String email = "admin@email.com";

        when(appProperties.getAdminEmail()).thenReturn(email);
        doNothing().when(emailSender).sendEmail(email, subject, body);

        emailAdminService.sendEmail(subject, body);

        verify(emailSender).sendEmail(email, subject, body);
    }
}
