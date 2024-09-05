package com.forum.project.presentation;

import com.forum.project.domain.User;
import lombok.Getter;

@Getter
public class CustomUserInfoDto {
    private Long id;
    private String userId;
    private String email;
    private String password;
    private String name;

    public CustomUserInfoDto(String userId, String email, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public CustomUserInfoDto(Long id, String userId, String email, String password, String name) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    static public CustomUserInfoDto toDto(User user) {
        return new CustomUserInfoDto(user.getId(), user.getUserId(), user.getEmail(), user.getPassword(),
                user.getName());
    }
}
