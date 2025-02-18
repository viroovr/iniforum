package com.forum.project.domain.auth.repository;

public interface TokenBlacklistHandler {
    void blacklistAccessToken(String token, long ttl);
    void blacklistRefreshToken(String token, long ttl);
    boolean isBlacklistedAccessToken(String token);
    boolean isBlacklistedRefreshToken(String token);
}
