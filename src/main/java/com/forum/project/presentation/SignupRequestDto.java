package com.forum.project.presentation;

import com.forum.project.domain.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupRequestDto {

    @NotNull
    private String userId;

    @NotNull
    private String email;

    @NotNull
    @Max(20)
    @Min(12)
    private String password;

    @NotNull
    private String name;

    public SignupRequestDto(String userId, String email, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    static public User toUser(SignupRequestDto signupRequestDto) {
        return new User(signupRequestDto.userId, signupRequestDto.email, signupRequestDto.password,
                signupRequestDto.name);
    }

    static public SignupRequestDto toDto(User user) {
        return new SignupRequestDto(user.getUserId(), user.getEmail(), user.getPassword(),
                user.getName());
    }
}
