package com.forum.project.presentation.user;


import com.forum.project.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    private String password;

    private String profileImagePath;

    private String nickname;

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getPassword(),
                user.getProfileImagePath(),
                user.getNickname()
        );
    }
}
