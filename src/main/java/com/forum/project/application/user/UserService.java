package com.forum.project.application.user;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.domain.exception.InvalidPasswordException;
import com.forum.project.presentation.auth.LoginRequestDto;
import com.forum.project.presentation.auth.SignupRequestDto;
import com.forum.project.presentation.user.UserInfoDto;
import com.forum.project.presentation.user.UserRequestDto;
import com.forum.project.presentation.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfoDto getUserProfile(String token) {
        String jwt = jwtTokenProvider.extractTokenByHeader(token);
        Long id = jwtTokenProvider.getId(jwt);
        User user = userRepository.findById(id);

        return UserInfoDto.toDto(user);
    }

    public UserResponseDto updateUserProfile(String token, UserRequestDto userRequestDto) {
        String jwt = jwtTokenProvider.extractTokenByHeader(token);
        User user = userRepository.findById(jwtTokenProvider.getId(jwt));
        checkPassword(userRequestDto, user);

        String newPassword = passwordEncoder.encode(userRequestDto.getNewPassword());

        user.setPassword(newPassword);
        user.setNickname(userRequestDto.getNickname());
        user.setProfileImagePath(userRequestDto.getProfileImagePath());

        return UserResponseDto.toDto(userRepository.update(user));
    }

    private void checkPassword(UserRequestDto userRequestDto, User user) {
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }
}
