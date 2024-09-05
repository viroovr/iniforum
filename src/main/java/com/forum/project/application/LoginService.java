package com.forum.project.application;

import com.forum.project.domain.User;
import com.forum.project.presentation.SignupRequestDto;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.SignupResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
        if (userRepository.findByEmail(signupRequestDto.getEmail()) != null) {
            return null;
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        signupRequestDto.setPassword(encodedPassword);
        User user = SignupRequestDto.toUser(signupRequestDto);

        return SignupResponseDto.toDto(userRepository.save(user));
    }
}
