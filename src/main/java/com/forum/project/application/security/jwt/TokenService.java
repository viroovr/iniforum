package com.forum.project.application.security.jwt;

import com.forum.project.application.user.UserRole;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class TokenService {

    private final Key key;

    @Getter
    private final long accessTokenExpTime;

    @Getter
    private final long refreshTokenExpTime;

    public TokenService(
            @Value("${jwt.secret}") String secret_key,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime

    ) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret_key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public Long getId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public String getLoginId(String token) {
        return parseClaims(token).get("loginId", String.class);
    }

    public UserRole getRole(String token) {
        return parseClaims(token).get("role", UserRole.class);
    }

    private String createToken(UserInfoDto member, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("loginId", member.getLoginId());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(UserInfoDto member) {
        return createToken(member, accessTokenExpTime);
    }

    public String createRefreshToken(UserInfoDto member) {
        return createToken(member, refreshTokenExpTime);
    }

    public long getExpirationTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis() / 1000;
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public String regenerateAccessToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(claims.get("id", Long.class))
                .loginId(claims.get("loginId", String.class))
                .email(claims.get("email", String.class))
                .role(claims.get("role", UserRole.class))
                .build();

        return createAccessToken(userInfoDto);

    }

    public String extractTokenByHeader(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
