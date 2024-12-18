package com.forum.project.domain.repository;

import com.forum.project.domain.entity.User;

import java.util.Optional;

public interface UserRepository{
    Optional<User> findById(Long id);
    Optional<User> findByUserLoginId(String userId);
    User save(User user);
    User update(User user);

    boolean emailExists(String email);

    boolean userLoginIdExists(String userId);
}
