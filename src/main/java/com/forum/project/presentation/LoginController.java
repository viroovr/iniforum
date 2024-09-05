package com.forum.project.presentation;

import com.forum.project.application.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private final LoginService loginService;

    public LoginController (LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> requestSignup(
            @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto createdUser = loginService.createUser(signupRequestDto);
        if (createdUser != null) {
            return ResponseEntity.ok("User created");
        } else {
            return ResponseEntity.badRequest().body("User Created Failed");
        }
    }


}
