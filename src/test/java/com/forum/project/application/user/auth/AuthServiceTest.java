//package com.forum.project.application.user.auth;
//
//import com.forum.project.application.user.auth.AuthService;
//import com.forum.project.application.user.auth.UserPasswordService;
//import com.forum.project.infrastructure.jwt.AccessRedisTokenBlacklistHandler;
//import com.forum.project.infrastructure.jwt.RefreshRedisTokenBlacklistHandler;
//import com.forum.project.application.jwt.TokenService;
//import com.forum.project.domain.user.User;
//import com.forum.project.application.exception.ApplicationException;
//import com.forum.project.application.exception.ErrorCode;
//import com.forum.project.infrastructure.persistence.user.UserRepository;
//import com.forum.project.presentation.dtos.TestDtoFactory;
//import com.forum.project.presentation.auth.LoginRequestDto;
//import com.forum.project.presentation.auth.SignupRequestDto;
//import com.forum.project.presentation.auth.SignupResponseDto;
//import com.forum.project.presentation.dtos.TokenResponseDto;
//import com.forum.project.presentation.user.UserInfoDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private UserPasswordService userPasswordService;
//    @Mock
//    private TokenService tokenService;
//    @Mock
//    private RefreshRedisTokenBlacklistHandler refreshTokenBlacklistService;
//    @Mock
//    private AccessRedisTokenBlacklistHandler accessTokenBlacklistService;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private SignupRequestDto signupRequestDto;
//    private LoginRequestDto loginRequestDto;
//    private User mockUser;
//
//    @BeforeEach
//    void setUp() {
//        signupRequestDto = TestDtoFactory.createSignupRequestDto();
//        loginRequestDto = TestDtoFactory.createLoginRequestDto();
//        mockUser = TestDtoFactory.createUserEntity();
//    }
//
//    @Test
//    void shouldSignupSuccessfully_WhenValidRequestSignupProvided() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
//        when(userRepository.userLoginIdExists(signupRequestDto.getLoginId())).thenReturn(false);
//        when(userRepository.save(any(User.class))).thenReturn(mockUser);
//        when(userPasswordService.encode(signupRequestDto.getPassword())).thenReturn(mockUser.getPassword());
//
//        SignupResponseDto responseDto = authService.createUser(signupRequestDto);
//
//        assertNotNull(responseDto);
//        assertEquals("test@test.com", responseDto.getEmail());
//        assertEquals("testId", responseDto.getLoginId());
//        assertEquals("testLastName", responseDto.getLastName());
//        assertEquals("testFirstName", responseDto.getFirstName());
//        verify(userRepository).emailExists(signupRequestDto.getEmail());
//        verify(userRepository).userLoginIdExists(signupRequestDto.getLoginId());
//        verify(userPasswordService).encode(signupRequestDto.getPassword());
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void shouldThrowEmailAlreadyExistException_WhenEmailAlreadyExists() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(true);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.createUser(signupRequestDto));
//
//        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, applicationException.getErrorCode());
//        verify(userRepository).emailExists(signupRequestDto.getEmail());
//    }
//
//    @Test
//    void shouldThrowUserIdAlreadyExistException_WhenUserIdAlreadyExists() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
//        when(userRepository.userLoginIdExists(signupRequestDto.getLoginId())).thenReturn(true);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.createUser(signupRequestDto));
//
//        assertEquals(ErrorCode.LOGIN_ID_ALREADY_EXISTS, applicationException.getErrorCode());
//        verify(userRepository).emailExists(signupRequestDto.getEmail());
//        verify(userRepository).userLoginIdExists(signupRequestDto.getLoginId());
//    }
//
//    @Test
//    void shouldLogInSuccessfully_WhenValidRequestLoginProvided() {
//        when(userRepository.findByUserLoginId(signupRequestDto.getLoginId()))
//                .thenReturn(Optional.of(mockUser));
//        doNothing().when(userPasswordService)
//                .validatePassword(signupRequestDto.getPassword(), mockUser.getPassword());
//        when(tokenService.createAccessToken(any(UserInfoDto.class)))
//                .thenReturn("access-token");
//        when(tokenService.createRefreshToken(any(UserInfoDto.class)))
//                .thenReturn("refresh-token");
//
//        TokenResponseDto responseDto = authService.loginUserWithTokens(loginRequestDto);
//
//        assertNotNull(responseDto);
//        assertEquals("access-token", responseDto.getAccessToken());
//        assertEquals("refresh-token", responseDto.getRefreshToken());
//        verify(userRepository).findByUserLoginId(signupRequestDto.getLoginId());
//        verify(userPasswordService).validatePassword(
//                signupRequestDto.getPassword(), mockUser.getPassword());
//        verify(tokenService).createAccessToken(any(UserInfoDto.class));
//        verify(tokenService).createRefreshToken(any(UserInfoDto.class));
//    }
//
//    @Test
//    void shouldLogOutSuccessfully_WhenValidUserRequestsLogout() {
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//
//        when(tokenService.isValidToken(any(String.class))).thenReturn(true);
//        when(tokenService.getExpirationTime(anyString())).thenReturn(100L);
//        doNothing().when(accessTokenBlacklistService).blacklistToken(accessToken, 100L);
//        doNothing().when(refreshTokenBlacklistService).blacklistToken(refreshToken, 100L);
//
//        authService.logout(accessToken, refreshToken);
//
//        verify(accessTokenBlacklistService).blacklistToken(accessToken, 100L);
//        verify(refreshTokenBlacklistService).blacklistToken(refreshToken, 100L);
//        verify(tokenService, times(2)).isValidToken(any(String.class));
//    }
//
//    @Test
//    void shouldThrowAuthInvalidTokenException_WhenRefreshTokenIsNotValid() {
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//        when(tokenService.isValidToken(refreshToken)).thenReturn(false);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.logout(accessToken, refreshToken));
//
//        assertEquals(ErrorCode.AUTH_INVALID_TOKEN, applicationException.getErrorCode());
//        verify(tokenService).isValidToken(refreshToken);
//    }
//
//    @Test
//    void shouldThrowAuthInvalidTokenException_WhenAccessTokenIsNotValid() {
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//        when(tokenService.isValidToken(refreshToken)).thenReturn(true);
//        when(tokenService.isValidToken(accessToken)).thenReturn(false);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.logout(accessToken, refreshToken));
//
//        assertEquals(ErrorCode.AUTH_INVALID_TOKEN, applicationException.getErrorCode());
//        verify(tokenService).isValidToken(accessToken);
//    }
//
//    @Test
//    void shouldRefreshAccessTokenSuccessfully_WhenValidRefreshTokenProvided() {
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//
//        when(refreshTokenBlacklistService.isBlacklistedToken(refreshToken)).thenReturn(false);
//        when(tokenService.isValidToken(refreshToken)).thenReturn(true);
//        when(tokenService.regenerateAccessToken(refreshToken)).thenReturn(accessToken);
//
//        TokenResponseDto tokenResponseDto = authService.refreshAccessToken(refreshToken);
//
//        assertNull(tokenResponseDto.getRefreshToken());
//        assertEquals("access-token", tokenResponseDto.getAccessToken());
//        verify(tokenService).regenerateAccessToken(refreshToken);
//        verify(refreshTokenBlacklistService).isBlacklistedToken(refreshToken);
//        verify(tokenService).isValidToken(refreshToken);
//    }
//
//    @Test
//    void shouldThrowAuthBlacklistTokenException_WhenRefreshTokenIsRefreshed() {
//        String refreshToken = "refresh-token";
//        when(refreshTokenBlacklistService.isBlacklistedToken(refreshToken)).thenReturn(true);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.refreshAccessToken(refreshToken));
//
//        assertEquals(ErrorCode.AUTH_BLACKLISTED_REFRESH_TOKEN, applicationException.getErrorCode());
//        verify(refreshTokenBlacklistService).isBlacklistedToken(refreshToken);
//    }
//
//    @Test
//    void shouldThrowAuthInvalidTokenException_WhenRefreshTokenIsRefreshed() {
//        String refreshToken = "refresh-token";
//        when(refreshTokenBlacklistService.isBlacklistedToken(refreshToken)).thenReturn(false);
//        when(tokenService.isValidToken(refreshToken)).thenReturn(false);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.refreshAccessToken(refreshToken));
//
//        assertEquals(ErrorCode.AUTH_INVALID_TOKEN, applicationException.getErrorCode());
//        verify(refreshTokenBlacklistService).isBlacklistedToken(refreshToken);
//        verify(tokenService).isValidToken(refreshToken);
//    }
//}