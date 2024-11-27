package com.forum.project.presentation.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.controller.AuthController;
import com.forum.project.presentation.dtos.auth.LoginRequestDto;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import com.forum.project.presentation.exception.ExceptionResponseUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.request.WebRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private ExceptionResponseUtil exceptionResponseUtil;

    private final String requestSignupUriPath = "/api/v1/auth/signup";
    private final String requestLoginUriPath = "/api/v1/auth/login";
    private final SignupRequestDto signupRequestDto = new SignupRequestDto("user1", "email@example.com", "password1_", "홍길동");
    private final SignupResponseDto signupResponseDto = new SignupResponseDto("user1", "email@example.com", "홍길동");
    private final LoginRequestDto loginRequestDto = new LoginRequestDto("user1", "password1_");
    private final TokenResponseDto tokens = new TokenResponseDto("access-token", "refresh-token");

    @BeforeEach
    void setup() {
        doCallRealMethod().when(exceptionResponseUtil).createErrorResponsev2(any(String.class), any(String.class), any(HttpStatus.class), any(WebRequest.class));
    }
    @Test
    void testRequestSignup_Success() throws Exception {
        when(authService.createUser(any(SignupRequestDto.class))).thenReturn(signupResponseDto);

        mockMvc.perform(post(requestSignupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.email").value("email@example.com"))
                .andExpect(jsonPath("$.name").value("홍길동"));

        verify(authService, times(1)).createUser(any(SignupRequestDto.class));
    }

    private void verifyErrorResponse(ResultActions resultActions, ErrorCode errorCode, String uriPath) throws Exception {
        resultActions
                .andExpect(jsonPath("$.error").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void testRequestSignup_UserIdAlreadyExistsException() throws Exception {
        when(authService.createUser(any(SignupRequestDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.USER_ID_ALREADY_EXISTS));

        ResultActions result = mockMvc.perform(post(requestSignupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)))
                .andDo(print())
                .andExpect(status().isConflict());

        verifyErrorResponse(result, ErrorCode.USER_ID_ALREADY_EXISTS, requestSignupUriPath);
    }

    @Test
    void testRequestSignup_EmailAlreadyExistsException() throws Exception {
        when(authService.createUser(any(SignupRequestDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS));

        ResultActions result = mockMvc.perform(post(requestSignupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)))
                .andExpect(status().isConflict());

        verifyErrorResponse(result, ErrorCode.EMAIL_ALREADY_EXISTS, requestSignupUriPath);
    }

    @Test
    void testRequestLogin_Success() throws Exception {
        when(authService.loginUserWithTokens(any(LoginRequestDto.class))).thenReturn(tokens);
        when(cookieService.createRefreshTokenCookie("refresh-token")).thenReturn(new Cookie("refreshToken", "refresh-token"));

        mockMvc.perform(post(requestLoginUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokens)))
                .andExpect(cookie().value("refreshToken", "refresh-token"));

        verify(authService, times(1)).loginUserWithTokens(any(LoginRequestDto.class));
        verify(cookieService, times(1)).createRefreshTokenCookie(any());
    }

    @Test
    void testRequestLogin_InvalidPasswordException() throws Exception {
        when(authService.loginUserWithTokens(any(LoginRequestDto.class)))
                .thenThrow(new ApplicationException(ErrorCode.AUTH_INVALID_PASSWORD));

        ResultActions result = mockMvc.perform(post(requestLoginUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDto)))
                .andExpect(status().isConflict());

        verifyErrorResponse(result, ErrorCode.AUTH_INVALID_PASSWORD, requestLoginUriPath);

        verify(authService, times(1)).loginUserWithTokens(any(LoginRequestDto.class));
        verify(cookieService, never()).createRefreshTokenCookie(any());
    }

    @Test
    public void testLogout() throws Exception {
        String logoutUriPath = "/api/v1/auth/logout";
        String jwt = "Bearer jwt-token";
        String refreshToken = "refresh-token";

        when(cookieService.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
        doNothing().when(authService).logout(eq(jwt), eq(refreshToken));
        doCallRealMethod().when(cookieService).clearRefreshToken(any(HttpServletResponse.class));

        mockMvc.perform(post(logoutUriPath)
                        .header("Authorization", jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"))
                .andExpect(cookie().value("refreshToken", ""));

        verify(authService, times(1)).logout(eq(jwt), eq(refreshToken));
        verify(cookieService, times(1)).clearRefreshToken(any(HttpServletResponse.class));
        verify(cookieService, times(1)).getRefreshTokenFromCookies(any());

    }

    @Test
    public void testRefreshAccessToken() throws Exception {
        String refreshToken = "refresh-token";
        TokenResponseDto tokenResponseDto = new TokenResponseDto("access-token", null);

        when(cookieService.getRefreshTokenFromCookies(any())).thenReturn(refreshToken);
        when(authService.refreshAccessToken(refreshToken)).thenReturn(tokenResponseDto);

        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokenResponseDto)));

        verify(authService, times(1)).refreshAccessToken(eq(refreshToken));
        verify(cookieService, times(1)).getRefreshTokenFromCookies(any());
    }
}
