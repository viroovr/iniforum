package com.forum.project.presentation.dtos.token;

import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.*;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDto extends BaseResponseDto {
    private String accessToken;
    private String refreshToken;
}
