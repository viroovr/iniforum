package com.forum.project.infrastructure.email;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.application.exception.InfraErrorCode;
import com.forum.project.application.exception.InfraException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSender emailSender;

    @Test
    void shouldSendEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String subject = "Test Email";
        String body = "<h1>This is a test email.</h1>";
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        emailSender.sendEmail(to, subject, body);

        verify(mailSender).createMimeMessage();
        assertEquals(mimeMessage.getSubject(), subject);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    public void shouldThrowFailSendingEmailException_whenFailCreateHelper() throws MessagingException{
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        doAnswer(invocation -> {
            throw new MessagingException("Message creation failed");
        }).when(mailSender).send(any(MimeMessage.class));

        InfraException infraException = assertThrows(InfraException.class,
                () -> emailSender.sendEmail(to, subject, body));

        assertEquals(InfraErrorCode.FAIL_SENDING_EMAIL, infraException.getErrorCode());
    }
}