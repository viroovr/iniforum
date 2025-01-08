package com.forum.project.application.user.auth;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final PasswordEncoder passwordEncoder;

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApplicationException(ErrorCode.AUTH_INVALID_PASSWORD);
        }
    }

    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
