package com.forum.project.presentation.auth;

import com.forum.project.domain.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupRequestDto {

    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 4, max = 20, message = "사용자 Id는 최소 4자 이상, 최대 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "사용자 ID는 알파벳으로 시작해야하며, 알파벳과 숫자만 포함할 수 있습니다.")
    private String userId;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상, 30자 이하이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+]).+$",
            message = "비밀번호는 최소 1개의 숫자, 1개의 알파벳, 1개의 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 최대 50자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "이름은 한글 또는 영문만 포함할 수 있습니다.")
    private String name;

    public SignupRequestDto(String userId, String email, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    static public User toUser(SignupRequestDto signupRequestDto) {
        return new User(
                signupRequestDto.userId,
                signupRequestDto.email,
                signupRequestDto.password,
                signupRequestDto.name
        );
    }

    static public SignupRequestDto toDto(User user) {
        return new SignupRequestDto(user.getUserId(), user.getEmail(), user.getPassword(),
                user.getName());
    }
}
