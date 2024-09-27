package com.forum.project.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository{
    User findById(Long id);
    User findByEmail(String email);
    User findByUserId(String userId);
    User save(User user);
}
