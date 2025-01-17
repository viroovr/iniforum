package com.forum.project.presentation.auth;

import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SignupResponseDto extends BaseResponseDto {
    private String loginId;
    private String email;
    private String lastName;
    private String firstName;
}
