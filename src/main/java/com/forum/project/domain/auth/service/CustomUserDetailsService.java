package com.forum.project.domain.auth.service;

import com.forum.project.domain.user.mapper.UserDtoMapper;
import com.forum.project.domain.user.entity.CustomUserDetails;
import com.forum.project.domain.user.entity.User;
import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) {
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        UserInfoDto userInfoDto = UserDtoMapper.toUserInfoDto(user);
        return new CustomUserDetails(userInfoDto);
    }
}
