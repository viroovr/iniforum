package com.forum.project.application.scheduler;

import com.forum.project.application.user.UserDeactivationScheduler;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserDeactivationSchedulerTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDeactivationScheduler userDeactivationScheduler;

    @Test
    public void testDeactivateInactiveUsers() {
        // Given: Mock User 데이터 설정
        Duration inactivityPeriod = Duration.ofDays(30);
        LocalDateTime thresholdDate = LocalDateTime.now().minus(inactivityPeriod);

        User inactiveUser = new User(); // Mock User 객체 생성
        inactiveUser.setId(1L);
        inactiveUser.setLastActivityDate(thresholdDate.minusDays(1));
        inactiveUser.setStatus("ACTIVE");

        when(userRepository.findAllByLastActivityDateBefore(thresholdDate))
                .thenReturn(Collections.singletonList(inactiveUser));
        when(userRepository.updateAll(anyList())).thenReturn(new int[] {1});

        // When: 스케줄러 강제 실행
        userDeactivationScheduler.deactivateInactiveUsers();

    }
}