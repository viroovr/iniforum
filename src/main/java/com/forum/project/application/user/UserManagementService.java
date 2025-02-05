package com.forum.project.application.user;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.User;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.domain.user.UserRole;
import com.forum.project.domain.user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final Clock clock;

    public void deactivateInactiveUsers(Duration inactivityPeriod) {
        LocalDateTime thresholdDate = LocalDateTime.now(clock).minus(inactivityPeriod);

        List<User> inactiveUsers = userRepository.findAllByLastActivityDateBefore(thresholdDate);
        if (inactiveUsers.isEmpty()) {
            return;
        }

        List<Long> ids = inactiveUsers.stream().map(User::getId).toList();
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
