package com.forum.project.infrastructure.security;

import com.forum.project.application.TestService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserExtractionAspectTest.class, TestService.class})
class UserExtractionAspectTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TestService testService;

    @Test
    void extractUser_ShouldAddUserToMethodArguments() {
        // Arrange
        String token = "mocked_token";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLoginId("test_user");

        // Mock behavior for extracting user from token
        when(authenticationService.extractUserByHeader(token)).thenReturn(mockUser);

        // Act
        String result = testService.testMethod(token, mockUser);

        // Assert
        assertEquals("User: test_user", result);

        // Verify extractUserByToken was called once
        verify(authenticationService, times(1)).extractUserByHeader(token);
    }
}
