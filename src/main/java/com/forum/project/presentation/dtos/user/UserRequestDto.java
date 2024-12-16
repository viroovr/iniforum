package com.forum.project.presentation.dtos.user;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String password;

    private String newPassword;

    private String nickname;
}
