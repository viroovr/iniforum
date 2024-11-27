package com.forum.project.application.auth;

import com.forum.project.application.security.UserPasswordService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.auth.SignupRequestDto;
import com.forum.project.presentation.dtos.auth.SignupResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordService userPasswordService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private SignupRequestDto signupRequestDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        signupRequestDto = SignupRequestDto.builder()
                .userId("user1")
                .email("email@example.com")
                .password("password1_")
                .name("홍길동")
                .build();
        mockUser = User.builder()
                .userId("user1")
                .email("email@example.com")
                .password("password1_")
                .name("홍길동")
                .nickname("user1")
                .build();
    }


    @Test
    void testCreateUser_Success() {
        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
        when(userRepository.userIdExists(signupRequestDto.getUserId())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(userPasswordService.encode(signupRequestDto.getPassword())).thenReturn(mockUser.getPassword());

        SignupResponseDto responseDto = authService.createUser(signupRequestDto);

        assertNotNull(responseDto);
        assertEquals("email@example.com", responseDto.getEmail());
        assertEquals("user1", responseDto.getUserId());
        assertEquals("홍길동", responseDto.getName());
        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
        verify(userRepository, times(1)).userIdExists(signupRequestDto.getUserId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userPasswordService, times(1)).encode(signupRequestDto.getPassword());
    }

    @Test
    void testCreateUser_EmailAlreadyExits() {
        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(true);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> authService.createUser(signupRequestDto));

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, applicationException.getErrorCode());
        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
        verify(userRepository, never()).userIdExists(signupRequestDto.getUserId());
        verify(userRepository, never()).save(any(User.class));
        verify(userPasswordService, never()).encode(signupRequestDto.getPassword());
    }

    @Test
    void testCreateUser_UserIdAlreadyExist() {
        when(userRepository.emailExists(signupRequestDto.getEmail())).thenReturn(false);
        when(userRepository.userIdExists(signupRequestDto.getUserId())).thenReturn(true);

        ApplicationException applicationException = assertThrows(ApplicationException.class,
                () -> authService.createUser(signupRequestDto));

        assertEquals(ErrorCode.USER_ID_ALREADY_EXISTS, applicationException.getErrorCode());
        verify(userRepository, times(1)).emailExists(signupRequestDto.getEmail());
        verify(userRepository, times(1)).userIdExists(signupRequestDto.getUserId());
        verify(userRepository, never()).save(any(User.class));
        verify(userPasswordService, never()).encode(signupRequestDto.getPassword());
    }

}