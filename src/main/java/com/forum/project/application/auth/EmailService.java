package com.forum.project.application.auth;

import com.forum.project.application.security.RandomStringGenerator;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.dtos.EmailVerification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;
    private static final String SERVICE_NAME = "QAForum@qaforum.com";
    private static final String REDIS_PREFIX = "emailVerified:";

    public void sendVerificationCode(String to) {
        String code = RandomStringGenerator.generateRandomString(6);
        storeVerificationCode(to, code);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String subject = "Verification Email";
        String body = "<h1>Hello!</h1><p>Verification Code : " + code + "</p>";

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(SERVICE_NAME);

            mailSender.send(mimeMessage);
            log.debug("preparing message send done");
        } catch (MessagingException e) {
            throw new ApplicationException(ErrorCode.FAIL_SENDING_EMAIL);
        }
    }

    public void storeVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(REDIS_PREFIX + email,
                new EmailVerification(code, false)
                ,3
                ,TimeUnit.MINUTES);
    }

    public void verifyCode(String email, String inputCode) {
        EmailVerification verification = (EmailVerification) redisTemplate.opsForValue().get(REDIS_PREFIX + email);
        if (verification == null || !verification.getVerificationCode().equals(inputCode)) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        verification.setVerified(true);
        redisTemplate.opsForValue().set(REDIS_PREFIX + email, verification, 10, TimeUnit.MINUTES);
    }

    public void verifyEmail(String email) {
        EmailVerification verification = (EmailVerification) redisTemplate.opsForValue().get(REDIS_PREFIX + email);
        if (verification == null || !verification.isVerified()) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }
}
