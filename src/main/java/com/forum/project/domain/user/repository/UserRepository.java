package com.forum.project.domain.user.repository;

import com.forum.project.domain.user.dto.UserCreateDto;
import com.forum.project.domain.user.entity.User;
import com.forum.project.domain.user.vo.UserKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository{
    Optional<UserKey> insertAndReturnGeneratedKeys(UserCreateDto dto);

    Optional<User> findById(Long id);
    Optional<User> findByLoginId(String userId);
    Optional<User> findByEmail(String email);
    List<User> findAllByLastActivityDateBefore(LocalDateTime thresholdDate);
    List<User> searchByLoginIdAndStatus(String keyword, String status, int page, int size);

    String getLoginIdById(Long id);

    Long countByLoginIdAndStatus(String keyword, String status);

    int updateProfile(User user);
    int updateAllStatus(List<Long> userIds, List<String> statuses);

    boolean existsById(Long id);
    boolean existsByEmail(String email);
    boolean existsByLoginId(String userId);

    void delete(Long id);
}
