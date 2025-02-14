package com.forum.project.application;

import com.forum.project.domain.user.entity.User;
import com.forum.project.infrastructure.security.auth.ExtractUser;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @ExtractUser
    public String testMethod(String token, User user) {
        return "User: " + user.getLastName();
    }
}
