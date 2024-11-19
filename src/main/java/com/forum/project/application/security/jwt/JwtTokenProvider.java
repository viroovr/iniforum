package com.forum.project.application.security.jwt;

import com.forum.project.presentation.dtos.user.UserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret_key,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime

    ) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret_key);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    public String createAccessToken(UserInfoDto member) {
        return createToken(member, accessTokenExpTime);
    }

    public String createRefreshToken(UserInfoDto member) {return createToken(member, refreshTokenExpTime);}

    private String createToken(UserInfoDto member, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("id", member.getId());
        claims.put("userId", member.getUserId());
        claims.put("email", member.getEmail());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    public String getUserId(String token) {return parseClaims(token).get("userId", String.class);}

    public long getExpirationTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis() / 1000;
    }

    public boolean validateToken(String token) {
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
        if (validateToken(refreshToken)) {
            Claims claims = parseClaims(refreshToken);
            UserInfoDto userInfoDto = new UserInfoDto(
                    claims.get("id", Long.class),
                    claims.get("userId", String.class),
                    claims.get("email", String.class)
            );

            return createAccessToken(userInfoDto);
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }

    public String extractTokenByHeader(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
