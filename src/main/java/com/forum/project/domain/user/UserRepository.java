package com.forum.project.domain.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository{
    Optional<User> findById(Long id);
    Optional<User> findByUserLoginId(String userId);
    Optional<User> findByEmail(String email);
    List<User> findAllByLastActivityDateBefore(LocalDateTime thresholdDate);
    User save(User user);
    User update(User user);
    int[] updateAll(List<User> users);

    boolean emailExists(String email);

    boolean userLoginIdExists(String userId);
}
