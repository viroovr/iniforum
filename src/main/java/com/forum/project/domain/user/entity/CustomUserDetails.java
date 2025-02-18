package com.forum.project.domain.user.entity;

import com.forum.project.domain.user.vo.UserStatus;
import com.forum.project.domain.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserInfoDto userInfoDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String userRole = userInfoDto.getRole();
        String role = userRole != null ? userRole : "USER";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return userInfoDto.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfoDto.getUserId().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !UserStatus.LOCKED.name()
                .equalsIgnoreCase(userInfoDto.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        LocalDateTime passwordLastModifiedDate = userInfoDto.getPasswordLastModifiedDate();
        if (passwordLastModifiedDate == null) {
            return true;
        }
        return passwordLastModifiedDate.isAfter(LocalDateTime.now().minusDays(90));
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.name().equalsIgnoreCase(userInfoDto.getStatus());
    }
}
