package com.forum.project.domain.email.service;

import com.forum.project.core.common.RandomStringGenerator;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.entity.EmailVerification;
import com.forum.project.domain.auth.repository.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final RandomStringGenerator randomStringGenerator;

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_TIME = 3;
    private static final int REGISTER_EXPIRATION_TIME = 10;

    private String generateVerificationCode() {
        return randomStringGenerator.generate(CODE_LENGTH);
    }

    private void saveVerificationCode(String email, String code) {
        verificationCodeService.save(email, new EmailVerification(code, false), CODE_EXPIRATION_TIME);
    }

    public void sendVerificationCode(String toEmail) {
        String code = generateVerificationCode();
        saveVerificationCode(toEmail, code);
        emailService.sendVerificationEmail(toEmail, code);
    }

    private EmailVerification getVerificationByEmail(String email) {
        EmailVerification verification = verificationCodeService.get(email);
        if (verification == null) {
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE,
                    String.format("'%s' 키에 대해 저장된 값이 없습니다.", email));
        }
        return verification;
    }

    public void verifyEmailCode(String email, String inputCode) {
        EmailVerification verification = getVerificationByEmail(email);
        verification.validateCodeMatch(inputCode);
        verification.verify();
        verificationCodeService.save(email, verification, REGISTER_EXPIRATION_TIME);
    }

    public void validateEmailCode(String email) {
        EmailVerification verification = getVerificationByEmail(email);
        verification.validateVerified();
    }
}
