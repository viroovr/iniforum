package com.forum.project.presentation;

import com.forum.project.application.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private final AuthService authService;

    public LoginController (AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> requestSignup(
            @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto createdUser = authService.createUser(signupRequestDto);
        if (createdUser != null) {
            return ResponseEntity.ok("User created");
        } else {
            return ResponseEntity.badRequest().body("User Created Failed");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> requestLogin(
            @RequestBody LoginRequestDto loginRequestDto
    ) {
        String token = authService.loginUser(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }



}
