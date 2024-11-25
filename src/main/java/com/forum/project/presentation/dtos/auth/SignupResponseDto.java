package com.forum.project.presentation.dtos.auth;

import com.forum.project.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {

    private String userId;
    private String email;
    private String name;

    static public SignupResponseDto toDto(User user) {
        return new SignupResponseDto(user.getUserId(), user.getEmail(), user.getName());
    }
}
