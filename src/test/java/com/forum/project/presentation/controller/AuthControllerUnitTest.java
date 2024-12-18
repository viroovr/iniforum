package com.forum.project.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import com.forum.project.application.auth.EmailService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.auth.*;
import com.forum.project.presentation.dtos.token.TokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private EmailService emailService;

    private final String signupUriPath = "/api/v1/auth/signup";
    private final String loginUriPath = "/api/v1/auth/login";
    private final String sendEmailUriPath = "/api/v1/auth/send-email";
    private final String verifyEmailUriPath = "/api/v1/auth/verify-email";
    private final String logoutUriPath = "/api/v1/auth/logout";
    private EmailRequestDto emailRequestDto;
    private SignupRequestDto signupRequestDto;
    private SignupResponseDto signupResponseDto;
    private LoginRequestDto loginRequestDto;
    private TokenResponseDto tokenResponseDto;

    @BeforeEach
    void setUp() {
        emailRequestDto = TestDtoFactory.createEmailRequestDto();
        signupRequestDto = TestDtoFactory.createSignupRequestDto();
        signupResponseDto = TestDtoFactory.createSignupResponseDto();
        loginRequestDto = TestDtoFactory.createLoginRequestDto();
        tokenResponseDto = TestDtoFactory.createTokenResponseDto();
    }

    @Test
    void testSendEmail_Success() throws Exception {
        String email = emailRequestDto.getEmail();

        doNothing().when(emailService).sendVerificationCode(email);

        mockMvc.perform(post(sendEmailUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification email sent"));

        verify(emailService).sendVerificationCode(email);
    }

    @Test
    void testVerifyEmail_Success() throws Exception {
        String email = emailRequestDto.getEmail();
        String code = emailRequestDto.getCode();

        doNothing().when(emailService).verifyCode(email, code);

        mockMvc.perform(post(verifyEmailUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(emailService).verifyCode(email, code);
    }

    @Test
    void testRequestSignup_Success() throws Exception {
        when(authService.createUser(signupRequestDto)).thenReturn(signupResponseDto);
        doNothing().when(emailService).verifyEmail(signupRequestDto.getEmail());

        mockMvc.perform(post(signupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(signupResponseDto)));

        verify(authService).createUser(signupRequestDto);
        verify(emailService).verifyEmail(signupRequestDto.getEmail());
    }

    @Test
    void testRequestLogin_Success() throws Exception {
        String refreshToken = tokenResponseDto.getRefreshToken();

        when(authService.loginUserWithTokens(loginRequestDto)).thenReturn(tokenResponseDto);
        when(cookieService.createRefreshTokenCookie(refreshToken))
                .thenReturn(new Cookie("refreshToken", refreshToken));

        mockMvc.perform(post(loginUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokenResponseDto)))
                .andExpect(cookie().value("refreshToken", refreshToken));

        verify(authService).loginUserWithTokens(loginRequestDto);
        verify(cookieService).createRefreshTokenCookie(refreshToken);
    }

    @Test
    public void testLogout_Success() throws Exception {
        String header = "Bearer jwt-token";
        String refreshToken = "refresh-token";
        String accessToken = "access-token";

        when(tokenService.extractTokenByHeader(header)).thenReturn(accessToken);
        when(cookieService.getRefreshTokenFromCookies(any(HttpServletRequest.class)))
                .thenReturn(refreshToken);
        doNothing().when(authService).logout(accessToken, refreshToken);

        mockMvc.perform(post(logoutUriPath)
                        .cookie(new Cookie("refreshToken", refreshToken))
                        .header("Authorization", header))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"))
                .andExpect(cookie().value("refreshToken", ""));

        verify(tokenService).extractTokenByHeader(header);
        verify(authService).logout(accessToken, refreshToken);
        verify(cookieService).getRefreshTokenFromCookies(any(HttpServletRequest.class));
    }

    @Test
    public void testRefreshAccessToken_Success() throws Exception {
        String refreshToken = "refresh-token";
        String refreshUriPath = "/api/v1/auth/refresh";
        tokenResponseDto.setAccessToken(null);

        when(cookieService.getRefreshTokenFromCookies(any(HttpServletRequest.class))).thenReturn(refreshToken);
        when(authService.refreshAccessToken(refreshToken)).thenReturn(tokenResponseDto);

        mockMvc.perform(post(refreshUriPath)
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(tokenResponseDto)));

        verify(authService).refreshAccessToken(eq(refreshToken));
        verify(cookieService).getRefreshTokenFromCookies(any(HttpServletRequest.class));
    }

    private void verifyErrorResponse(ResultActions resultActions, ErrorCode errorCode, String uriPath) throws Exception {
        resultActions
                .andExpect(status().is(errorCode.getStatus().value()))
                .andExpect(jsonPath("$.error").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void testSendEmail_FailureSendingEmailException() throws Exception {
        ErrorCode errorCode = ErrorCode.FAIL_SENDING_EMAIL;
        String email = emailRequestDto.getEmail();

        doThrow(new ApplicationException(errorCode))
                .when(emailService).sendVerificationCode(email);

        ResultActions result = mockMvc.perform(post(sendEmailUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailRequestDto)));

        verify(emailService).sendVerificationCode(email);
        verifyErrorResponse(result, errorCode, sendEmailUriPath);
    }

    @Test
    void testSendEmail_InvalidSendingEmailException() throws Exception {
        ErrorCode errorCode = ErrorCode.INVALID_VERIFICATION_CODE;
        String email = emailRequestDto.getEmail();
        String code = emailRequestDto.getCode();

        doThrow(new ApplicationException(errorCode)).when(emailService).verifyCode(email, code);

        ResultActions result = mockMvc.perform(post(verifyEmailUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailRequestDto)));

        verifyErrorResponse(result, errorCode, verifyEmailUriPath);
    }

    @Test
    void testRequestSignup_UserIdAlreadyExistsException() throws Exception {
        ErrorCode errorCode = ErrorCode.LOGIN_ID_ALREADY_EXISTS;

        when(authService.createUser(signupRequestDto)).thenThrow(new ApplicationException(errorCode));

        ResultActions result = mockMvc.perform(post(signupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)));

        verifyErrorResponse(result, errorCode, signupUriPath);
    }

    @Test
    void testRequestSignup_EmailAlreadyExistsException() throws Exception {
        ErrorCode errorCode = ErrorCode.EMAIL_ALREADY_EXISTS;

        when(authService.createUser(signupRequestDto))
                .thenThrow(new ApplicationException(errorCode));

        ResultActions result = mockMvc.perform(post(signupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)));

        verifyErrorResponse(result, errorCode, signupUriPath);
    }

    @Test
    void testRequestSignup_EmailNotValidated() throws Exception {
        ErrorCode errorCode = ErrorCode.FAIL_SENDING_EMAIL;

        doThrow(new ApplicationException(errorCode))
                .when(emailService).verifyEmail(signupRequestDto.getEmail());

        ResultActions result = mockMvc.perform(post(signupUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupRequestDto)));

        verifyErrorResponse(result, errorCode, signupUriPath);
    }

    @Test
    void testRequestLogin_InvalidPasswordException() throws Exception {
        ErrorCode errorCode = ErrorCode.AUTH_INVALID_PASSWORD;

        when(authService.loginUserWithTokens(loginRequestDto))
                .thenThrow(new ApplicationException(errorCode));

        ResultActions result = mockMvc.perform(post(loginUriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequestDto)));

        verifyErrorResponse(result, errorCode, loginUriPath);
    }
}
