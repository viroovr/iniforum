package com.forum.project.domain.user.service;

import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final AuthorizationService authorizationService;
    private final UserManagementService userManagementService;
    private final UserSearchService userSearchService;

    public void updateUserRole(Long userId, String newRole) {
        userManagementService.updateUserRole(userId, newRole);
    }

    @Transactional
    public void reactivateAccount(String header) {
        User user = authorizationService.extractUserByHeader(header);
        userManagementService.reactivateAccount(user);
    }

    public Page<UserInfoDto> searchUsers(String keyword, String role, int offset, int limit) {
        return userSearchService.searchUsers(keyword, role, offset, limit);
    }
}
