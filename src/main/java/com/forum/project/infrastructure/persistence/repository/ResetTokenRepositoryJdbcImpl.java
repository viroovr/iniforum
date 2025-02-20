package com.forum.project.infrastructure.persistence.repository;

import com.forum.project.domain.auth.entity.ResetToken;
import com.forum.project.domain.auth.repository.ResetTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ResetTokenRepositoryJdbcImpl implements ResetTokenRepository {
    @Override
    public int save(ResetToken resetToken) {
        return 0;
    }

    @Override
    public boolean existsByToken(String token) {
        return false;
    }

    @Override
    public void deleteByToken(String token) {

    }

    @Override
    public Optional<ResetToken> findByToken(String token) {
        return Optional.empty();
    }
}
