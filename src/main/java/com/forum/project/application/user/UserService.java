package com.forum.project.application.user;

import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.domain.exception.InvalidPasswordException;
import com.forum.project.presentation.dtos.user.UserInfoDto;
import com.forum.project.presentation.dtos.user.UserRequestDto;
import com.forum.project.presentation.dtos.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String uploadDir = "src/main/resources/static/profile-images/";

    @Autowired
    public UserService(TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfoDto getUserProfile(String token) {
        String jwt = tokenService.extractTokenByHeader(token);
        Long id = tokenService.getId(jwt);
        User user = userRepository.findById(id);

        return UserInfoDto.toDto(user);
    }

    public UserResponseDto updateUserProfile(String token, UserRequestDto userRequestDto, MultipartFile file) throws IOException {
        String jwt = tokenService.extractTokenByHeader(token);
        User user = userRepository.findById(tokenService.getId(jwt));
        checkPassword(userRequestDto, user);

        String newPassword = passwordEncoder.encode(userRequestDto.getNewPassword());
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destinationFile = new File(uploadDir + filename);
        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            throw new IOException("Failed to transfer the file: " + e.getMessage(), e);
        }

        user.setPassword(newPassword);
        user.setNickname(userRequestDto.getNickname());
        user.setProfileImagePath("/profile-images/" + filename);

        return UserResponseDto.toDto(userRepository.update(user));
    }

    private void checkPassword(UserRequestDto userRequestDto, User user) {
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }
    }
}
