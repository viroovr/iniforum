package com.forum.project.domain.user.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.entity.UserActivityLog;
import com.forum.project.domain.user.repository.UserActivityLogRepository;
import com.forum.project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
                .timestamp(Instant.now())
                .action(action)
                .build();

        userActivityLogRepository.save(log);
    }
}
