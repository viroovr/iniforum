package com.forum.project.presentation.auth;

import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponseDto extends BaseResponseDto {
    private String loginId;
    private String email;
    private String lastName;
    private String firstName;
}
