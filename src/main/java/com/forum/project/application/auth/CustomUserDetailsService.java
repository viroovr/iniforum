package com.forum.project.application.auth;

import com.forum.project.application.converter.UserDtoConverterFactory;
import com.forum.project.domain.user.User;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.presentation.user.UserInfoDto;
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

        UserInfoDto userInfoDto = UserDtoConverterFactory.toUserInfoDto(user);
        return new CustomUserDetails(userInfoDto);
    }
}
