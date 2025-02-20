package com.forum.project.domain.user.controller;

import com.forum.project.domain.bookmark.service.QuestionBookmarkService;
import com.forum.project.domain.user.dto.UserInfoDto;
import com.forum.project.domain.user.dto.UserRequestDto;
import com.forum.project.domain.user.dto.UserResponseDto;
import com.forum.project.domain.user.service.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;
    private final QuestionBookmarkService questionBookmarkService;

    @GetMapping(value = "/profile")
    public ResponseEntity<UserInfoDto> getUserProfile(
            @RequestHeader("Authorization") String header
    ) {
        UserInfoDto user = userFacade.getUserProfileByHeader(header);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> updateUserProfile (
            @RequestHeader("Authorization") String header,
            @ModelAttribute UserRequestDto userRequestDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile file
    ) throws IOException{
        UserResponseDto userResponseDto = userFacade.updateUserProfileByHeader(header, userRequestDto, file);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }
}
