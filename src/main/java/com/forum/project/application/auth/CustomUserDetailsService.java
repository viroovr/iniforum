package com.forum.project.application.auth;

import com.forum.project.domain.entity.User;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.user.CustomUserDetails;
import com.forum.project.presentation.dtos.user.UserInfoDto;
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
        User user = userRepository.findById(Long.parseLong(id));

        UserInfoDto userInfoDto = UserInfoDto.toDto(user);
        return new CustomUserDetails(userInfoDto);
    }
}
