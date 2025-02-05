package com.forum.project.domain.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository{
    Map<String, Object> insertAndReturnGeneratedKeys(User user);

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
