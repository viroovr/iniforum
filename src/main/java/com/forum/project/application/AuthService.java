package com.forum.project.application;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.User;
import com.forum.project.presentation.CustomUserInfoDto;
import com.forum.project.presentation.LoginRequestDto;
import com.forum.project.presentation.SignupRequestDto;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.SignupResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository) {
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

    @Transactional
    public String loginUser(LoginRequestDto loginRequestDto) {
        String userId = loginRequestDto.getUserId();
        String password = loginRequestDto.getPassword();
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        CustomUserInfoDto info = CustomUserInfoDto.toDto(user);

        return jwtTokenProvider.createAccessToken(info);

    }
}
