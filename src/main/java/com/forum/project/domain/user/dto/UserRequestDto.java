package com.forum.project.domain.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    private String password;

    private String newPassword;

    private String nickname;
}
