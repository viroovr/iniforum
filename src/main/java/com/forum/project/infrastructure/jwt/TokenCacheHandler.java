package com.forum.project.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenCacheHandler {
    private final TokenCacheRepository tokenCacheRepository;
    private final JwtUtils jwtUtils;

    public Claims getClaims(String token) {
        return (Claims) tokenCacheRepository.get(token)
                .orElseGet(() -> {
                    Claims claims = jwtUtils.parseClaims(token);
                    tokenCacheRepository.put(token, claims);
                    return claims;
                });
    }

    public void cacheToken(String token, Claims claims) {
        tokenCacheRepository.put(token, claims);
    }

    public Optional<Claims> getTokenFromCache(String token) {
        return tokenCacheRepository.get(token);
    }

    public <T> T extractClaim(String token, String claimKey, Class<T> claimType) {
        Claims claims = getClaims(token);
        return claims.get(claimKey, claimType);
    }

    public Date getExpirationDate(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    public ClaimRequestDto extractClaimsByToken(String token) {
        Claims claims = getClaims(token);
        return new ClaimRequestDto(claims);
    }
}
