package com.forum.project.presentation.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 4, max = 20, message = "잘못된 형식입니다.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "잘못된 형식입니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
