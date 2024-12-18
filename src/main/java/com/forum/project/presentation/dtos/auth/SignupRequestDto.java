package com.forum.project.presentation.dtos.auth;

import com.forum.project.domain.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequestDto {

    @NotBlank(message = "{loginId.required}")
    @Size(min = 4, max = 20, message = "{loginId.length}")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "{loginId.pattern}")
    private String loginId;

    @NotBlank(message = "{email.required}")
    @Email(message = "{email.invalid}")
    private String email;

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 30, message = "{password.length}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+]).+$",
            message = "{password.pattern}")
    private String password;

    @NotBlank(message = "{name.required}")
    @Size(min = 1, max = 50, message = "{name.length}")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "{name.length}")
    private String lastName;

    @NotBlank(message = "{name.required}")
    @Size(min = 1, max = 50, message = "{name.length}")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "{name.length}")
    private String firstName;
}
