package com.forum.project.presentation.auth;

import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.controller.AuthController;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import com.forum.project.presentation.exception.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private CookieService cookieService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testRequestSignup() throws Exception{
        when(authService.createUser(any(SignupRequestDto.class)))
                .thenReturn(new SignupResponseDto("user1", "email@example.com","홍길동"));

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"user1\", \"email\": \"email@example.com\", \"password\": \"password1_\", \"name\": \"홍길동\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.email").value("email@example.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));

        verify(authService, times(1)).createUser(any(SignupRequestDto.class));
    }

    private void verifyErrorResponse(ResultActions resultActions, ErrorCode errorCode, String path) throws  Exception{
        resultActions
                .andExpect(jsonPath("$.error").value(errorCode.getCode())) // errorCode 검증
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.path").value(path)); // 요청 경로 검증
    }
    @Test
    void testRequestSignup_EmailAlreadyExists() throws Exception {
        when(authService.createUser(any(SignupRequestDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS));

        ResultActions result = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"userId\", \"email\": \"email@example.com\", \"password\": \"password1_\", \"name\": \"name\"}"))
                .andExpect(status().isConflict());

        verifyErrorResponse(result, ErrorCode.EMAIL_ALREADY_EXISTS, "/api/v1/auth/signup");

    }

    @Test
    void testRequestSignup_UserIdAlreadyExistsException() throws Exception {
        when(authService.createUser(any(SignupRequestDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.USER_ID_ALREADY_EXISTS));

        ResultActions result = mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"userId\", \"email\": \"email@example.com\", \"password\": \"password1_\", \"name\": \"name\"}"))
                .andExpect(status().isConflict());

        verifyErrorResponse(result, ErrorCode.USER_ID_ALREADY_EXISTS, "/api/v1/auth/signup");
    }

    @Test
    void requestLogin() throws Exception{
        Map<String, String> tokens = Map.of("accessToken", "access-token", "refreshToken", "refresh-token");
        when(authService.loginUserWithTokens(any(LoginRequestDto.class))).thenReturn(tokens);
        when(cookieService.createRefreshTokenCookie("refresh-token")).thenReturn(new Cookie("refreshToken", "refresh-token"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"userId\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":\"access-token\"}"));

        verify(authService, times(1)).loginUserWithTokens(any(LoginRequestDto.class));
        verify(cookieService, times(1)).addCookieToResponse(any(), any());
    }

    @Test
    public void testLogout() throws Exception {
        String jwt = "Bearer jwt-token";
        String refreshToken = "refresh-token";
        when(cookieService.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
        when(authService.validateRefreshToken(refreshToken)).thenReturn(true);
        doNothing().when(authService).logout(any(String.class), any(String.class), any(Long.class));

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));

        verify(authService, times(1)).logout(any(String.class), any(String.class), any(Long.class));
        verify(cookieService, times(1)).clearRefreshTokenCookie(any());
    }

    @Test
    public void testRefreshAccessToken() throws Exception {
        String refreshToken = "refresh-token";
        String newAccessToken = "new-access-token";
        when(cookieService.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
        when(authService.refreshAccessToken(refreshToken)).thenReturn(newAccessToken);

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accessToken\":\"new-access-token\"}"));

        verify(authService, times(1)).refreshAccessToken(refreshToken);
    }
}