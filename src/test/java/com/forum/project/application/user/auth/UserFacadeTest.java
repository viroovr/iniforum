//package com.forum.project.application.auth;
//
//import com.forum.project.application.email.EmailService;
//import com.forum.project.application.user.auth.UserPasswordService;
//import com.forum.project.domain.user.infrastructure.FileService;
//import com.forum.project.domain.auth.service.TokenService;
//import com.forum.project.domain.user.service.UserFacade;
//import com.forum.project.domain.user.*;
//import com.forum.project.domain.user.repository.UserActivityLogRepository;
//import com.forum.project.domain.user.repository.UserRepository;
//import com.forum.project.domain.user.dto.UserInfoDto;
//import com.forum.project.domain.user.dto.UserRequestDto;
//import com.forum.project.domain.user.dto.UserResponseDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.*;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserFacadeTest {
//    @InjectMocks
//    private UserFacade userFacade;
//
//    @Mock
//    private TokenService tokenService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserPasswordService passwordService;
//
//    @Mock
//    private FileService fileService;
//
//    @Mock
//    private EmailService emailService;
//
//    @Mock
//    private UserActivityLogRepository userActivityLogRepository;
//
//    private Clock fixedClock;
//
//    @BeforeEach
//    void setUp() {
//        fixedClock = Clock.fixed(Instant.parse("2024-12-19T00:00:00Z"), ZoneId.of("UTC"));
//        userFacade = new UserFacade(tokenService, userRepository, passwordService,
//                fileService, emailService, userActivityLogRepository, fixedClock);
//    }
//
//    @Test
//    void testGetUserProfileByHeader() {
//        // Given
//        String header = "Bearer token";
//        Long userId = 1L;
//        User user = new User();
//        user.setId(userId);
//        user.setNickname("TestUser");
//        when(tokenService.extractTokenByHeader(header)).thenReturn("token");
//        when(tokenService.getId("token")).thenReturn(userId);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // When
//        UserInfoDto result = userFacade.getUserProfileByHeader(header);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("TestUser", result.getNickname());
//        verify(userRepository, times(1)).findById(userId);
//    }
//
//    @Test
//    void testUpdateUserProfileByHeader() throws IOException {
//        // Given
//        String header = "Bearer token";
//        Long userId = 1L;
//        User user = new User();
//        user.setId(userId);
//        user.setPassword("encodedPassword");
//
//        UserRequestDto requestDto = new UserRequestDto();
//        requestDto.setPassword("oldPassword");
//        requestDto.setNewPassword("newPassword");
//        requestDto.setNickname("NewNickname");
//
//        MultipartFile file = mock(MultipartFile.class);
//        when(tokenService.extractTokenByHeader(header)).thenReturn("token");
//        when(tokenService.getId("token")).thenReturn(userId);
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        doNothing().when(passwordService).validatePassword("oldPassword", "encodedPassword");
//        when(passwordService.encode("newPassword")).thenReturn("newEncodedPassword");
//        when(fileService.uploadFile(file)).thenReturn("uploadedFilePath");
//
//        // When
//        UserResponseDto result = userFacade.updateUserProfileByHeader(header, requestDto, file);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("NewNickname", result.getNickname());
//        verify(userRepository, times(1)).update(user);
//    }
//
//    @Test
//    void testDeactivateInactiveUsers() {
//        // Given
//        Duration inactivityPeriod = Duration.ofDays(30);
//        LocalDateTime thresholdDate = LocalDateTime.now(fixedClock).minus(inactivityPeriod);
//
//        User inactiveUser = new User();
//        inactiveUser.setId(1L);
//        inactiveUser.setStatus(UserStatus.ACTIVE.name());
//        inactiveUser.setLastActivityDate(thresholdDate.minusDays(1));
//
//        when(userRepository.findAllByLastActivityDateBefore(thresholdDate))
//                .thenReturn(List.of(inactiveUser));
//        when(userRepository.updateAll(anyList())).thenReturn(new int[0]);
//
//        // When
//        userFacade.deactivateInactiveUsers(inactivityPeriod);
//
//        // Then
//        assertEquals(UserStatus.INACTIVE.name(), inactiveUser.getStatus());
//        verify(userRepository, times(1)).updateAll(anyList());
//    }
//
//    @Test
//    void testRequestPasswordReset() {
//        // Given
//        String email = "test@example.com";
//        User user = new User();
//        user.setId(1L);
//        user.setEmail(email);
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
//        when(tokenService.createPasswordResetToken(any(UserInfoDto.class))).thenReturn("resetToken");
//
//        // When
//        userFacade.requestPasswordReset(email);
//
//        // Then
//        verify(emailService, times(1))
//                .sendPasswordResetEmail(eq(email), eq("resetToken"));
//    }
//
//    @Test
//    void testResetPassword() {
//        // Given
//        String token = "validToken";
//        String newPassword = "newPassword";
//
//        User user = new User();
//        user.setId(1L);
//        when(tokenService.isValidToken(token)).thenReturn(true);
//        when(tokenService.getId(token)).thenReturn(1L);
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(passwordService.encode(newPassword)).thenReturn("encodedNewPassword");
//
//        // When
//        userFacade.resetPassword(token, newPassword);
//
//        // Then
//        assertEquals("encodedNewPassword", user.getPassword());
//        verify(userRepository, times(1)).update(user);
//    }
//
//    @Test
//    void testLogUserActivity() {
//        // Given
//        Long userId = 1L;
//        String action = "LOGIN";
//
//        User user = new User();
//        user.setId(userId);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // When
//        userFacade.logUserActivity(userId, action);
//
//        // Then
//        ArgumentCaptor<UserActivityLog> captor = ArgumentCaptor.forClass(UserActivityLog.class);
//        verify(userActivityLogRepository).save(captor.capture());
//        assertEquals(userId, captor.getValue().getUserId());
//        assertEquals(action, captor.getValue().getAction());
//    }
//}