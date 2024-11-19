package com.forum.project.presentation.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String password;

    private String newPassword;

    private String nickname;

//    public static User toEntity(UserRequestDto userRequestDto) {
//        return new User(
//                userRequestDto.getPassword(),
//                userRequestDto.getProfileImagePath(),
//                userRequestDto.getNickname()
//        );
//    }
}
