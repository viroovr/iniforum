package com.forum.project.domain.auth.repository;

import com.forum.project.domain.auth.entity.ResetToken;

import java.util.Optional;

public interface ResetTokenRepository {
    int save(ResetToken resetToken);
    boolean existsByToken(String token);
    void deleteByToken(String token);
    Optional<ResetToken> findByToken(String token);
}
