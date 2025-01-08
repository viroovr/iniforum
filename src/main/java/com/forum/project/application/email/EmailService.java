package com.forum.project.application.email;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.RandomStringGenerator;
import com.forum.project.domain.email.EmailVerification;
import com.forum.project.infrastructure.config.AppProperties;
import com.forum.project.infrastructure.email.EmailSender;
import com.forum.project.infrastructure.persistence.email.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final VerificationCodeStore verificationCodeStore;
    private final EmailSender emailSender;
    private final RandomStringGenerator randomStringGenerator;
    private final AppProperties appProperties;

    public void sendVerificationCode(String toEmail) {
        String code = randomStringGenerator.generate(6);
        verificationCodeStore.saveVerificationCode(toEmail,
                new EmailVerification(code, false),
                3);

        String subject = "Verification Email";
        String body = "<h1>Hello!</h1><p>Verification Code: " + code + "</p>";
        emailSender.sendEmail(toEmail, subject, body);
    }

    public void resendVerificationCode(String toEmail) {
        String code = randomStringGenerator.generate(6);
        verificationCodeStore.saveVerificationCode(toEmail,
                new EmailVerification(code, false), 3);

        String subject = "Verification Email (Re-send)";
        String body = "<h1>Hello!</h1><p>Verification Code: " + code + "</p>";
        emailSender.sendEmail(toEmail, subject, body);
    }

    public void sendEmailChangeVerification(String oldEmail, String newEmail) {
        String code = randomStringGenerator.generate(6);
        verificationCodeStore.saveVerificationCode(newEmail,
                new EmailVerification(code, false), 3);

        String subject = "Email Change Verification";
        String body = "<h1>Hello!</h1><p>Your verification code for changing email is: " + code + "</p>";
        emailSender.sendEmail(newEmail, subject, body);
    }

    public void sendPasswordResetEmail(String userEmail, String resetToken) {
        String resetLink = appProperties.getUrl() + "/reset-password?token=" + resetToken;
        String subject = "비밀번호 재설정 요청";
        String emailContent = "<p>안녕하세요,</p>" +
                "<p>아래 링크를 클릭하여 비밀번호를 재설정하세요:</p>" +
                "<a href=\"" + resetLink + "\">비밀번호 재설정 링크</a>" +
                "<p>해당 링크는 일정 시간 후 만료됩니다.</p>" +
                "<p>감사합니다.</p>";

        emailSender.sendEmail(userEmail, subject, emailContent);
    }

    public void retrySendEmail(String toEmail, String subject, String body, int retryCount) {
        for (int i = 0; i < retryCount; i++) {
            try {
                emailSender.sendEmail(toEmail, subject, body);
                break; // 성공하면 종료
            } catch (Exception e) {
                if (i == retryCount - 1) {
                    throw new ApplicationException(ErrorCode.FAIL_SENDING_EMAIL);
                }
            }
        }
    }

    public void validateEmailFormat(String email) {
        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL);
        }
    }

    public void sendNotification(String userEmail, String subject, String body) {
        emailSender.sendEmail(userEmail, subject, body);
    }

    public void verifyCode(String email, String inputCode) {
        EmailVerification verification = verificationCodeStore.getVerificationCode(email);
        if (verification == null || !verification.getVerificationCode().equals(inputCode)) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
        verification.setVerified(true);
        verificationCodeStore.updateVerificationCode(email, verification, 10);
    }

    public void verifyEmail(String email) {
        EmailVerification verification = verificationCodeStore.getVerificationCode(email);
        if (verification == null || !verification.isVerified()) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }
}
