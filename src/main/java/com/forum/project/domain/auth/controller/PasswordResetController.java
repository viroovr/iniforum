package com.forum.project.domain.auth.controller;

import com.forum.project.domain.user.service.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {
    private final UserFacade userFacade;

    @PostMapping("/request")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        userFacade.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userFacade.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }
}
