package com.forum.project.application.email;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.common.utils.RandomStringGenerator;
import com.forum.project.domain.email.EmailVerification;
import com.forum.project.infrastructure.email.EmailSender;
import com.forum.project.infrastructure.redis.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final VerificationCodeStore verificationCodeStore;
    private final EmailSender emailSender;
    private final RandomStringGenerator randomStringGenerator;

    public void sendVerificationCode(String toEmail) {
        String code = randomStringGenerator.generate(6);
        verificationCodeStore.saveVerificationCode(toEmail,
                new EmailVerification(code, false),
                3);

        String subject = "Verification Email";
        String body = "<h1>Hello!</h1><p>Verification Code: " + code + "</p>";
        emailSender.sendEmail(toEmail, subject, body);
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
