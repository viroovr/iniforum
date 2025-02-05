package com.forum.project.application.user.admin;

import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.application.user.UserManagementService;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final AuthenticationService authenticationService;
    private final UserManagementService userManagementService;
    private final UserSearchService userSearchService;

    public void updateUserRole(Long userId, String newRole) {
        userManagementService.updateUserRole(userId, newRole);
    }

    @Transactional
    public void reactivateAccount(String header) {
        User user = authenticationService.extractUserByHeader(header);
        userManagementService.reactivateAccount(user);
    }

    public Page<UserInfoDto> searchUsers(String keyword, String role, int offset, int limit) {
        return userSearchService.searchUsers(keyword, role, offset, limit);
    }
}
