package com.forum.project.application.user.auth;

import com.forum.project.domain.auth.service.CustomUserDetailsService;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.presentation.dtos.TestDtoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_Success() {
        // Given
        User mockUser = TestDtoFactory.createUserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("1");

        // Then
        assertNotNull(userDetails);
        assertEquals("1", userDetails.getUsername());
        assertEquals("testPassword1!", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> customUserDetailsService.loadUserByUsername("1"));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}