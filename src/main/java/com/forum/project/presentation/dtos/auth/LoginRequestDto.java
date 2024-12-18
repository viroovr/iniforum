package com.forum.project.presentation.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDto {
    @NotBlank(message = "{loginID.required}")
    @Size(min = 4, max = 20, message = "{loginID.length}")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "{loginID.pattern}")
    private String loginId;

    @NotBlank(message = "{password.required}")
    private String password;
}
