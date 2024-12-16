package com.forum.project.application.converter;

import com.forum.project.domain.entity.User;
import com.forum.project.presentation.dtos.user.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .profileImagePath(user.getProfileImagePath())
                .nickname(user.getNickname())
                .build();
    }
}
