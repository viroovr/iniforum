package com.forum.project.domain.auth.entity;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {
    private String verificationCode;
    private boolean verified;

    public void verify() {
        this.verified = true;
    }

    public void validateCodeMatch(String inputCode) {
        if (!this.verificationCode.equals(inputCode))
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE,
                    "입력받은 코드와 저장된 코드가 일치하지 않습니다."
            );
    }

    public void validateVerified() {
        if (!this.verified)
            throw new ApplicationException(ErrorCode.INVALID_VERIFICATION_CODE,
                    "인증되지 않은 이메일입니다."
            );
    }
}
