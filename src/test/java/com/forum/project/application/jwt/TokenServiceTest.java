package com.forum.project.application.jwt;

import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.auth.dto.ClaimRequestDto;
import com.forum.project.infrastructure.jwt.JwtUtils;
import com.forum.project.infrastructure.jwt.TokenCacheHandler;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.domain.user.dto.UserInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private TokenCacheHandler tokenCacheHandler;
    @Mock
    private JwtUtils jwtUtils;
    private long accessTokenExpTime;
    private long refreshTokenExpTime;
    private long passwordResetTokenExpTime;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        accessTokenExpTime = 3600L;
        refreshTokenExpTime = 64800;
        passwordResetTokenExpTime = 360L;
        this.tokenService = new TokenService(
                tokenCacheHandler,
                jwtUtils,
                accessTokenExpTime,
                refreshTokenExpTime,
                passwordResetTokenExpTime
        );
    }

    @Test
    void testGetUserId_success() {
        String token = "validToken";
        when(tokenCacheHandler.extractClaim(token, ClaimRequestDto.USER_ID_CLAIM_KEY, Long.class))
                .thenReturn(1L);

        Long response = tokenService.getUserId(token);

        assertNotNull(response);
        assertEquals(1L, response);
    }

    @Test
    void testGetLoginId_success() {
        String token = "validToken";
        when(tokenCacheHandler.extractClaim(token, ClaimRequestDto.LOGIN_ID_CLAIM_KEY, String.class))
                .thenReturn("validLoginId");

        String response = tokenService.getLoginId(token);

        assertNotNull(response);
        assertEquals("validLoginId", response);
    }

    @Test
    void testGetUserRole_success() {
        String token = "validToken";
        when(tokenCacheHandler.extractClaim(token, ClaimRequestDto.USER_ROLE_CLAIM_KEY, String.class))
                .thenReturn("validUserRole");

        String response = tokenService.getUserRole(token);

        assertNotNull(response);
        assertEquals("validUserRole", response);
    }

    @Test
    void testGetExpirationTime_success() {
        String token = "validToken";
        Date expirationDate = Date.from(LocalDateTime.now().plusSeconds(60).atZone(ZoneId.systemDefault()).toInstant());
        when(tokenCacheHandler.getExpirationDate(token)).thenReturn(expirationDate);

        long actual = tokenService.getExpirationTime(token);

        assertEquals(60, actual, 1);
    }

    @Test
    void testHasRole_success() {
        String token = "validToken";
        UserRole role = UserRole.USER;
        when(tokenCacheHandler.extractClaim(token, ClaimRequestDto.USER_ROLE_CLAIM_KEY, String.class))
                .thenReturn("USER");

        boolean actual = tokenService.hasRole(token, role);

        assertTrue(actual);
    }

    @Test
    void testCreateAccessToken_success() {
        UserInfoDto member = new UserInfoDto();
        when(jwtUtils.createToken(anyMap(), eq(accessTokenExpTime)))
                .thenReturn("accessToken");

        String actual = tokenService.createAccessToken(member);

        assertNotNull(actual);
        assertEquals("accessToken", actual);
    }

    @Test
    void testCreateRefreshToken_success() {
        UserInfoDto member = new UserInfoDto();
        when(jwtUtils.createToken(anyMap(), eq(refreshTokenExpTime)))
                .thenReturn("refreshToken");

        String actual = tokenService.createRefreshToken(member);

        assertNotNull(actual);
        assertEquals("refreshToken", actual);
    }

    @Test
    void testCreatePasswordResetToken_success() {
        UserInfoDto member = new UserInfoDto();
        when(jwtUtils.createToken(anyMap(), eq(passwordResetTokenExpTime)))
                .thenReturn("passwordResetToken");

        String actual = tokenService.createPasswordResetToken(member);

        assertNotNull(actual);
        assertEquals("passwordResetToken", actual);
    }

    @Test
    void testRegenerateAccessToken_success() {
        ClaimRequestDto dto = new ClaimRequestDto();
        String refreshToken = "refreshToken";
        when(tokenCacheHandler.extractClaimsByToken(refreshToken))
                .thenReturn(dto);
        when(jwtUtils.createToken(anyMap(), eq(accessTokenExpTime)))
                .thenReturn("regeneratedAccessToken");

        String actual = tokenService.regenerateAccessToken(refreshToken);

        assertNotNull(actual);
        assertEquals("regeneratedAccessToken", actual);
    }

    @Test
    void testRegenerateRefreshToken_success() {
        ClaimRequestDto dto = new ClaimRequestDto();
        String refreshToken = "refreshToken";
        when(tokenCacheHandler.extractClaimsByToken(refreshToken))
                .thenReturn(dto);
        when(jwtUtils.createToken(anyMap(), eq(refreshTokenExpTime)))
                .thenReturn("regeneratedRefreshToken");

        String actual = tokenService.regenerateRefreshToken(refreshToken);

        assertNotNull(actual);
        assertEquals("regeneratedRefreshToken", actual);
    }
}