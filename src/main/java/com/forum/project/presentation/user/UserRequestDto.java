package com.forum.project.presentation.user;

import com.forum.project.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String password;

    private String newPassword;

    private String profileImagePath;

    private String nickname;

    public static User toEntity(UserRequestDto userRequestDto) {
        return new User(
                userRequestDto.getPassword(),
                userRequestDto.getProfileImagePath(),
                userRequestDto.getNickname()
        );
    }
}
