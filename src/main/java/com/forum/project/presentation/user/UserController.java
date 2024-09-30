package com.forum.project.presentation.user;

import com.forum.project.application.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<UserInfoDto> getUserProfile(
            @RequestHeader("Authorization") String token
    ) {
        UserInfoDto user = userService.getUserProfile(token);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.PUT)
    public ResponseEntity<UserResponseDto> updateUserProfile (
            @RequestHeader("Authorization") String token,
            @RequestBody UserRequestDto userRequestDto
    ) {
        UserResponseDto userResponseDto = userService.updateUserProfile(token, userRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }


}
