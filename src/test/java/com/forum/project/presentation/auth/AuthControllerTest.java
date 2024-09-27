package com.forum.project.presentation.auth;

import com.forum.project.application.CookieService;
import com.forum.project.application.auth.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRequestSignup() throws Exception{
        SignupRequestDto signupRequestDto = new SignupRequestDto("userId", "email@example.com", "password1_", "name");
        when(authService.createUser(any(SignupRequestDto.class))).thenReturn(new SignupResponseDto("userId", "email@example.com","name"));

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"userId\", \"email\": \"email@example.com\", \"password\": \"password1_\", \"name\": \"name\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User created"));

        verify(authService, times(1)).createUser(any(SignupRequestDto.class));
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