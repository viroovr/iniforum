package com.forum.project.presentation.controller;

import com.forum.project.application.user.UserService;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.user.UserRequestDto;
import com.forum.project.presentation.dtos.user.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @RequestBody UserRequestDto userRequestDto,
            @RequestParam MultipartFile file
            ) throws IOException {

        UserResponseDto userResponseDto = userService.updateUserProfile(token, userRequestDto, file);
        return ResponseEntity.ok(userResponseDto);
    }


}
