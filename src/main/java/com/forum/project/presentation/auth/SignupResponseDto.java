package com.forum.project.presentation.auth;

import com.forum.project.domain.User;

public class SignupResponseDto {

    private String userId;
    private String email;
    private String name;

    public SignupResponseDto(String userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }


    static public SignupResponseDto toDto(User user) {
        return new SignupResponseDto(user.getUserId(), user.getEmail(), user.getName());
    }
}
