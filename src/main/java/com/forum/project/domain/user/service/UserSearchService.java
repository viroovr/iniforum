package com.forum.project.domain.user.service;

import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.mapper.UserDtoConverterFactory;
import com.forum.project.domain.user.repository.UserRepository;
import com.forum.project.domain.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchService {
    private final UserRepository userRepository;

    public Page<UserInfoDto> searchUsers(String keyword, String role, int offset, int limit) {
        List<User> users = userRepository.searchByLoginIdAndStatus(keyword, role, offset, limit);
        long total = userRepository.countByLoginIdAndStatus(keyword, role);
        List<UserInfoDto> userInfoDtos = users.stream()
                .map(UserDtoConverterFactory::toUserInfoDto)
                .toList();
        Pageable pageable = PageRequest.of(offset, limit);
        return new PageImpl<>(userInfoDtos, pageable, total);
    }
}