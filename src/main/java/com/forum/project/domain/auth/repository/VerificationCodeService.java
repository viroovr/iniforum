package com.forum.project.domain.auth.repository;

import com.forum.project.domain.auth.entity.EmailVerification;

public interface VerificationCodeService {
    void save(String key, EmailVerification verification, long duration);
    EmailVerification get(String key);
    boolean delete(String key);
}
