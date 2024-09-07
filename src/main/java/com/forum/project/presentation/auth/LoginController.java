package com.forum.project.presentation.auth;

import com.forum.project.application.auth.AuthService;
import com.forum.project.application.security.jwt.JwtBlacklistService;
import com.forum.project.application.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        long expirationTime = jwtTokenProvider.getExpirationTime(jwt);
        authService.logout(jwt, expirationTime);
        return ResponseEntity.ok("Logged out successfully");
    }



}
