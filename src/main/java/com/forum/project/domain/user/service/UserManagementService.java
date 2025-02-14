package com.forum.project.domain.user.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.vo.UserRole;
import com.forum.project.domain.user.vo.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    public void deactivateInactiveUsers(Duration inactivityPeriod) {
        LocalDateTime thresholdDate = Instant.ofEpochMilli(System.currentTimeMillis())
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime()
                                            .minus(inactivityPeriod);

        List<User> inactiveUsers = userRepository.findAllByLastActivityDateBefore(thresholdDate);
        if (inactiveUsers.isEmpty()) {
            return;
        }

        List<Long> ids = inactiveUsers.stream().map(User::getId).collect(Collectors.toList());
        List<String> statuses = Collections.nCopies(inactiveUsers.size(), UserStatus.INACTIVE.name());
        userRepository.updateAllStatus(ids, statuses);
    }

    public void reactivateAccount(User user) {
        if (!UserStatus.INACTIVE.name().equals(user.getStatus())) {
            throw new ApplicationException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }
        user.setStatus(UserStatus.ACTIVE.name());
        userRepository.updateProfile(user);
    }

    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!UserRole.isValid(newRole)) {
            throw new ApplicationException(ErrorCode.INVALID_USER_ROLE);
        }

        user.setRole(newRole);
        userRepository.updateProfile(user);
    }
}
