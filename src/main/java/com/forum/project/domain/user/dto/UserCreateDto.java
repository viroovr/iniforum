package com.forum.project.domain.user.dto;

import com.forum.project.domain.auth.dto.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {
    private String loginId;
    private String password;
    private String email;
    private String lastName;
    private String firstName;
    private String nickname;
    private String status;
    private String role;
    private String profileImagePath;

    public static UserCreateDtoBuilder fromSignupDto(SignupRequestDto dto) {
        return UserCreateDto.builder()
                .loginId(dto.getLoginId())
                .email(dto.getEmail())
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .nickname(dto.getLoginId());
    }
}
