package com.forum.project.presentation.user.admin;

import com.forum.project.application.user.admin.AdminFacade;
import com.forum.project.presentation.dtos.BaseResponseDto;
import com.forum.project.presentation.user.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacade adminFacade;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role/{userId}")
    public ResponseEntity<BaseResponseDto> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole
    ) {
        adminFacade.updateUserRole(userId, newRole);
        BaseResponseDto responseDto = new BaseResponseDto("User role updated successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/reactivate")
    public ResponseEntity<BaseResponseDto> reactivateAccount(
            @RequestHeader("Authorization") String header
    ) {
        adminFacade.reactivateAccount(header);
        BaseResponseDto responseDto = new BaseResponseDto("Reactivate user successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserInfoDto>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam int offset,
            @RequestParam int limit) {
        Page<UserInfoDto> users = adminFacade.searchUsers(keyword, role, status, offset, limit);
        return ResponseEntity.ok(users);
    }
}