package com.forum.project.infrastructure.jwt;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.domain.auth.vo.ClaimRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtils {

    private final Key key;

    public JwtUtils(
            @Value("${jwt.secret}") String secret_key
    ) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret_key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token");
            return e.getClaims();
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty", e);
        }
        return false;
    }

    public String createToken(ClaimRequest claimRequest, long expireTimeSec) {
        Map<String, Object> claims = claimRequest.toMap();

        ZonedDateTime now = ClockUtil.now().atZone(ZoneId.systemDefault());
        ZonedDateTime tokenValidity = now.plusSeconds(expireTimeSec);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
