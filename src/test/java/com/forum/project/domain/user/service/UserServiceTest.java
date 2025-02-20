package com.forum.project.domain.user.service;

import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.domain.auth.service.UserPasswordService;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.presentation.dtos.TestDtoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private UserFacade userFacade;

    private SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = TestDtoFactory.createSignupRequestDto();
    }

    private void mockValidateDuplicateEmail(boolean exists) {
        when(userRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(false);
    }

    private void mockValidateExistsLoginId(boolean exists) {
        when(userRepository.existsByEmail(signupRequestDto.getLoginId())).thenReturn(false);
    }

    @Test
    void createUser() {
    }

    @Test
    void findByLoginId() {
    }
}