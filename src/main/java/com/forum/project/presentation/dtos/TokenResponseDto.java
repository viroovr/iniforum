package com.forum.project.presentation.dtos;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDto extends BaseResponseDto {
    private String accessToken;
    private String refreshToken;
}
