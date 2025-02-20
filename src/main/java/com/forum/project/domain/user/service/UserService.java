package com.forum.project.domain.user.service;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.auth.dto.PasswordResetRequestDto;
import com.forum.project.domain.auth.dto.SignupResponseDto;
import com.forum.project.domain.user.dto.UserCreateDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.mapper.UserDtoMapper;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.vo.UserAction;
import com.forum.project.domain.user.vo.UserKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email))
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    private void validateExistsLoginId(String loginId) {
        if (userRepository.existsByEmail(loginId))
            throw new ApplicationException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
    }

    private UserKey insertAndReturnKeys(UserCreateDto createDto) {
        return userRepository.insertAndReturnGeneratedKeys(createDto)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DATABASE_ERROR,
                        "User 생성 이후, 키가 반환되지 않았습니다."));
    }

    @Transactional
    public SignupResponseDto createUser(UserCreateDto dto) {
        validateDuplicateEmail(dto.getEmail());
        validateExistsLoginId(dto.getLoginId());

        User preparedUser = UserDtoMapper.toEntity(dto, insertAndReturnKeys(dto));

        userFacade.logUserActivity(preparedUser.getId(), UserAction.SIGNUP_SUCCESS.name());
        return UserDtoMapper.toSignupResponseDto(preparedUser);
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) return;
        throw new ApplicationException(ErrorCode.EMAIL_NOT_FOUND);
    }

    public void updatePassword(PasswordResetRequestDto dto) {
        validateEmail(dto.getEmail());
        userRepository.updatePassword(dto.getEmail(), dto.getNewPassword());
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }
}
