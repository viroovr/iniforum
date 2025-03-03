package com.forum.project.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponseDto {
    private String loginId;
    private String email;
    private String lastName;
    private String firstName;
}
