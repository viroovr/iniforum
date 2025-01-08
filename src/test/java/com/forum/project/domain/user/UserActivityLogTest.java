package com.forum.project.domain.user;

import com.forum.project.application.user.UserFacade;
import com.forum.project.infrastructure.persistence.user.UserActivityLogRepository;
import com.forum.project.infrastructure.persistence.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserActivityLogTest {

    @Autowired
    private UserFacade userFacade;

    @MockBean
    private UserActivityLogRepository userActivityLogRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testLogUserActivity() {
        Long userId = 1L;
        String action = "LOGIN";
        User user = User.builder()
                .id(1L)
                .loginId("testLoginId")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userActivityLogRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userFacade.logUserActivity(userId, action);

        verify(userActivityLogRepository).save(any());
    }
}
