package com.forum.project.application.jwt;

import com.forum.project.core.common.ClockUtil;
import com.forum.project.domain.auth.dto.TokenResponseDto;
import com.forum.project.domain.auth.vo.TokenExpirationProperties;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.auth.vo.ClaimRequest;
import com.forum.project.infrastructure.jwt.JwtUtils;
import com.forum.project.infrastructure.jwt.TokenCacheHandler;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.testUtils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock private JwtUtils jwtUtils;
    @Mock private TokenCacheHandler tokenCacheHandler;
    @Mock private TokenExpirationProperties properties;

    @InjectMocks
    private TokenService tokenService;

    private final String accessToken = "accessToken";

    @BeforeEach
    void setUp() {
        TestUtils.setFixedClock();
    }

    @Test
    void getUserId() {
        when(tokenCacheHandler.extractClaim(accessToken, ClaimRequest.USER_ID_CLAIM_KEY, Long.class)).thenReturn(1L);

        Long result = tokenService.getUserId(accessToken);

        assertThat(result).isOne();
    }

    @Test
    void getLoginId() {
        when(tokenCacheHandler.extractClaim(accessToken, ClaimRequest.LOGIN_ID_CLAIM_KEY, String.class))
                .thenReturn("testLoginId");

        String result = tokenService.getLoginId(accessToken);

        assertThat(result).isEqualTo("testLoginId");
    }

    @Test
    void getExpirationTime() {
        Date expirationDate = Date.from(ClockUtil.now().plusSeconds(123).atZone(TestUtils.getZonedId()).toInstant());
        when(tokenCacheHandler.getExpirationDate(accessToken)).thenReturn(expirationDate);

        long result = tokenService.getExpirationTime(accessToken);

        assertThat(result).isEqualTo(123);
    }

    @Nested
    class UserRoleTests {

        private void mockGetUserRole(String role) {
            when(tokenCacheHandler.extractClaim(accessToken, ClaimRequest.USER_ROLE_CLAIM_KEY, String.class)).thenReturn(role);
        }

        @BeforeEach
        void setUpUserRoleTests() {
            mockGetUserRole(UserRole.USER.name());
        }

        @Test
        void getUserRole() {
            String result = tokenService.getUserRole(accessToken);

            assertThat(result).isEqualTo(UserRole.USER.name());
        }

        @Test
        void isAdmin() {
            mockGetUserRole(UserRole.ADMIN.name());
            boolean result = tokenService.isAdmin(accessToken);

            assertThat(result).isTrue();
        }

        @Test
        void isAdmin_false() {
            boolean result = tokenService.isAdmin(accessToken);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class TokenCreationTests {
        private static final long ACCESS_TOKEN_EXP_TIME = 3600L;
        private static final long REFRESH_TOKEN_EXP_TIME = 64800L;

        private final UserInfoDto mockUserInfoDto = mock(UserInfoDto.class);
        private final ClaimRequest mockClaimRequest = mock(ClaimRequest.class);

        private final String refreshToken = "refreshToken";

        @BeforeEach
        void setUp() {
            when(properties.getRefreshTokenExpTime()).thenReturn(REFRESH_TOKEN_EXP_TIME);
            when(properties.getAccessTokenExpTime()).thenReturn(ACCESS_TOKEN_EXP_TIME);
            when(jwtUtils.createToken(any(ClaimRequest.class), eq(REFRESH_TOKEN_EXP_TIME))).thenReturn(refreshToken);
            when(jwtUtils.createToken(any(ClaimRequest.class), eq(ACCESS_TOKEN_EXP_TIME))).thenReturn(accessToken);
        }

        @Test
        void createTokenResponseDto() {
            TokenResponseDto result = tokenService.createTokenResponseDto(mockUserInfoDto);

            assertAll(
                    () -> assertThat(result.getRefreshToken()).isEqualTo(refreshToken),
                    () -> assertThat(result.getAccessToken()).isEqualTo(accessToken)
            );
        }

        @Test
        void regenerateTokens() {
            when(tokenCacheHandler.extractClaimsByToken(refreshToken)).thenReturn(mockClaimRequest);

            TokenResponseDto result = tokenService.regenerateTokens(refreshToken);

            assertAll(
                    () -> assertThat(result.getRefreshToken()).isEqualTo(refreshToken),
                    () -> assertThat(result.getAccessToken()).isEqualTo(accessToken)
            );
        }
    }
}