package com.forum.project.presentation.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TokenResponseDto extends BaseResponseDto {
    private String accessToken;
    private String refreshToken;
}
