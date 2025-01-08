package com.forum.project.infrastructure.jwt;

public interface TokenBlacklistHandler {
    public void blacklistToken(String accessToken, long ttl);
    public boolean isBlacklistedToken(String token);
}
