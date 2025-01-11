package com.forum.project.application.user;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserActivityLog;
import com.forum.project.domain.user.UserActivityLogRepository;
import com.forum.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserActivityService {
    private final UserRepository userRepository;
    private final UserActivityLogRepository userActivityLogRepository;

    public void logUserActivity(Long userId, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserActivityLog log = UserActivityLog.builder()
                .userId(user.getId())
                .timestamp(LocalDateTime.now())
                .action(action)
                .build();
        userActivityLogRepository.save(log);
    }
}
