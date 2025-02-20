package com.forum.project.domain.auth.mapper;

import com.forum.project.domain.auth.vo.ClaimRequest;
import com.forum.project.domain.user.dto.UserInfoDto;

public class AuthDtoMapper {
    public static ClaimRequest toClaimRequest(UserInfoDto dto) {
        return ClaimRequest.builder()
                .userId(dto.getUserId())
                .userRole(dto.getRole())
                .loginId(dto.getLoginId())
                .build();
    }
}
