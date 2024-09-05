package com.forum.project.application;

import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.CustomUserDetails;
import com.forum.project.presentation.CustomUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(()->new UsernameNotFoundException("해당하는 유저가 없습니다."));

        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.toDto(user);
        System.out.println(customUserInfoDto);
        return new CustomUserDetails(customUserInfoDto);
    }
}
