package com.forum.project.domain.auth.dto;

import com.forum.project.core.base.BaseResponseDto;
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
