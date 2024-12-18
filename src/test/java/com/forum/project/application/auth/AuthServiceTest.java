//package com.forum.project.application.auth;
//
//import com.forum.project.application.security.UserPasswordService;
//import com.forum.project.application.security.jwt.AccessTokenService;
//import com.forum.project.application.security.jwt.RefreshTokenService;
//import com.forum.project.application.security.jwt.TokenService;
//import com.forum.project.domain.entity.User;
//import com.forum.project.domain.exception.ApplicationException;
//import com.forum.project.domain.exception.ErrorCode;
//import com.forum.project.domain.repository.UserRepository;
//import com.forum.project.presentation.dtos.auth.LoginRequestDto;
//import com.forum.project.presentation.dtos.auth.SignupRequestDto;
//import com.forum.project.presentation.dtos.auth.SignupResponseDto;
//import com.forum.project.presentation.dtos.auth.TestDtoFactory;
//import com.forum.project.presentation.dtos.token.TokenResponseDto;
//import com.forum.project.presentation.dtos.user.UserInfoDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserPasswordService userPasswordService;
//
//    @Mock
//    private RefreshTokenService refreshTokenService;
//
//    @Mock
//    private AccessTokenService accessTokenService;
//
//    @Mock
//    private TokenService tokenService;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private SignupRequestDto signupRequestDto;
//    private LoginRequestDto loginRequestDto;
//    private User mockUser;
//    private UserInfoDto mockUserInfoDto;
//
//    @BeforeEach
//    void setUp() {
//        signupRequestDto = TestDtoFactory.createSignupRequestDto();
//        loginRequestDto = TestDtoFactory.createLoginRequestDto();
//        mockUser = TestDtoFactory.createUserEntity();
//        mockUserInfoDto = TestDtoFactory.createUserInfoDto();
//    }
//
//
//    @Test
//    void testCreateUser_Success() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
//        when(userRepository.userLoginIdExists(signupRequestDto.getUserId())).thenReturn(false);
//        when(userRepository.save(any(User.class))).thenReturn(mockUser);
//        when(userPasswordService.encode(signupRequestDto.getPassword())).thenReturn(mockUser.getPassword());
//
//        SignupResponseDto responseDto = authService.createUser(signupRequestDto);
//
//        assertNotNull(responseDto);
//        assertEquals("email@example.com", responseDto.getEmail());
//        assertEquals("user1", responseDto.getUserId());
//        assertEquals("홍길동", responseDto.getName());
//        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
//        verify(userRepository, times(1)).userLoginIdExists(signupRequestDto.getUserId());
//        verify(userRepository, times(1)).save(any(User.class));
//        verify(userPasswordService, times(1)).encode(signupRequestDto.getPassword());
//    }
//
//    @Test
//    void testCreateUser_EmailAlreadyExits() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(true);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.createUser(signupRequestDto));
//
//        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, applicationException.getErrorCode());
//        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
//        verify(userRepository, never()).userLoginIdExists(signupRequestDto.getUserId());
//        verify(userRepository, never()).save(any(User.class));
//        verify(userPasswordService, never()).encode(signupRequestDto.getPassword());
//    }
//
//    @Test
//    void testCreateUser_UserIdAlreadyExist() {
//        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
//        when(userRepository.userLoginIdExists(signupRequestDto.getUserId())).thenReturn(true);
//
//        ApplicationException applicationException = assertThrows(ApplicationException.class,
//                () -> authService.createUser(signupRequestDto));
//
//        assertEquals(ErrorCode.LOGIN_ID_ALREADY_EXISTS, applicationException.getErrorCode());
//        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
//        verify(userRepository, times(1)).userLoginIdExists(signupRequestDto.getUserId());
//        verify(userRepository, never()).save(any(User.class));
//        verify(userPasswordService, never()).encode(signupRequestDto.getPassword());
//    }
//
//    @Test
//    void testLoginUser_Success() {
//        when(userRepository.findByUserLoginId(signupRequestDto.getUserId())).thenReturn(Optional.of(mockUser));
//        when(accessTokenService.createAccessToken(mockUserInfoDto)).thenReturn("access-token");
//        when(refreshTokenService.createRefreshToken(mockUserInfoDto)).thenReturn("refresh-token");
//        doNothing().when(userPasswordService).validatePassword(eq(signupRequestDto.getPassword()), eq(mockUser.getPassword()));
//
//        try (MockedStatic<UserInfoDto> mockedStatic = mockStatic(UserInfoDto.class)) {
//            mockedStatic.when(() ->UserInfoDto.toDto(mockUser)).thenReturn(mockUserInfoDto);
//
//            TokenResponseDto responseDto = authService.loginUserWithTokens(loginRequestDto);
//
//            assertNotNull(responseDto);
//            assertEquals("access-token", responseDto.getAccessToken());
//            assertEquals("refresh-token", responseDto.getRefreshToken());
//            verify(userRepository, times(1)).findByUserLoginId(signupRequestDto.getUserId());
//            verify(accessTokenService, times(1)).createAccessToken(eq(mockUserInfoDto));
//            verify(refreshTokenService, times(1)).createRefreshToken(eq(mockUserInfoDto));
//            verify(userPasswordService, times(1)).validatePassword(eq(signupRequestDto.getPassword()), eq(mockUser.getPassword()));
//            mockedStatic.verify(() -> UserInfoDto.toDto(eq(mockUser)), times(1));
//        }
//    }
//
//    @Test
//    void testLogout_Success() {
//        String authHeader = "Bearer access_token_example";
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//
//        when(accessTokenService.extractTokenByHeader(authHeader)).thenReturn(accessToken);
//        doNothing().when(accessTokenService).checkTokenValidity(accessToken);
//        doNothing().when(accessTokenService).revokeToken(accessToken);
//        doNothing().when(refreshTokenService).checkTokenValidity(refreshToken);
//        doNothing().when(refreshTokenService).revokeToken(refreshToken);
//
//        authService.logout(authHeader, refreshToken);
//
//        verify(accessTokenService).extractTokenByHeader(authHeader);
//        verify(refreshTokenService).checkTokenValidity(refreshToken);
//        verify(accessTokenService).checkTokenValidity(accessToken);
//        verify(accessTokenService).revokeToken(accessToken);
//        verify(refreshTokenService).revokeToken(refreshToken);
//    }
//
//    @Test
//    void testRefreshAccessToken_Success() {
//        String refreshToken = "refresh-token";
//        String accessToken = "access-token";
//        TokenResponseDto expectedTokenResponseDto = new TokenResponseDto(accessToken, null);
//
//        when(tokenService.regenerateAccessToken(refreshToken)).thenReturn(accessToken);
//        doNothing().when(refreshTokenService).checkTokenValidity(refreshToken);
//
//        TokenResponseDto actualTokenResponseDto = authService.refreshAccessToken(refreshToken);
//
//        assertEquals(expectedTokenResponseDto, actualTokenResponseDto);
//        verify(refreshTokenService).checkTokenValidity(refreshToken);
//        verify(tokenService).regenerateAccessToken(refreshToken);
//    }
//}