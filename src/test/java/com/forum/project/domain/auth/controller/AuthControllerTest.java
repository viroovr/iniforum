package com.forum.project.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.domain.auth.dto.*;
import com.forum.project.domain.auth.service.AuthService;
import com.forum.project.domain.email.service.EmailVerificationService;
import com.forum.project.infrastructure.security.CookieManager;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.TestDtoFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private AuthService authService;
    @MockBean private CookieManager cookieManager;
    @MockBean private EmailVerificationService emailVerificationService;

    private static final String BASE_PATH = "/api/v1/auth";

    private final String header = "Bearer accessToken";
    private SignupRequestDto signupRequestDto;
    private SignupResponseDto signupResponseDto;
    private LoginRequestDto loginRequestDto;
    private TokenResponseDto tokenResponseDto;
    private TokenRequestDto tokenRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = TestDtoFactory.createSignupRequestDto();
        signupResponseDto = TestDtoFactory.createSignupResponseDto();
        loginRequestDto = TestDtoFactory.createLoginRequestDto();
        tokenResponseDto = TestDtoFactory.createTokenResponseDto();
        tokenRequestDto = TestDtoFactory.createTokenRequestDto();
    }

    private ResultActions testRequestWithValidDto(Object dto, String endpoint) throws Exception {
        return mockMvc.perform(post(BASE_PATH + endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void requestSignup() throws Exception {
        when(authService.createUser(signupRequestDto)).thenReturn(signupResponseDto);

        testRequestWithValidDto(signupRequestDto, "/signup")
                .andExpect(content().json(new ObjectMapper().writeValueAsString(signupResponseDto)));
    }

    @Test
    void requestLogin() throws Exception {
        String refreshToken = tokenResponseDto.getRefreshToken();

        when(authService.loginUserWithTokens(loginRequestDto)).thenReturn(tokenResponseDto);
        when(cookieManager.createRefreshTokenCookie(refreshToken))
                .thenReturn(new Cookie("refreshToken", refreshToken));

        testRequestWithValidDto(loginRequestDto, "/login")
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokenResponseDto)))
                .andExpect(cookie().value("refreshToken", refreshToken));
    }

    private ResultActions testRequestByCookieAndHeader(String refreshToken, String endPoint) throws Exception {
        return mockMvc.perform(post(BASE_PATH + endPoint)
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void mockExtractTokens() {
        when(cookieManager.getRefreshTokenFromCookies(any(HttpServletRequest.class)))
                .thenReturn(tokenResponseDto.getRefreshToken());
    }

    @Test
    void logout() throws Exception {
        mockExtractTokens();
        when(cookieManager.createEmtpyRefreshTokenCookie()).thenReturn(new Cookie("refreshToken", ""));

        testRequestByCookieAndHeader(tokenResponseDto.getRefreshToken(), "/logout")
                .andExpect(cookie().value("refreshToken", ""));
    }

    @Test
    void refreshAccessToken() throws Exception {
        mockExtractTokens();
        when(authService.refreshAccessToken(tokenRequestDto)).thenReturn(tokenResponseDto);
        when(cookieManager.createRefreshTokenCookie(tokenResponseDto.getRefreshToken()))
                .thenReturn(new Cookie("refreshToken", tokenResponseDto.getRefreshToken()));

        testRequestByCookieAndHeader(tokenResponseDto.getRefreshToken(), "/refresh")
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokenResponseDto)));
    }
}
