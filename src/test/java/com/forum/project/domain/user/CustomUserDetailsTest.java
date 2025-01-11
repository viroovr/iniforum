package com.forum.project.domain.user;

import com.forum.project.presentation.user.UserInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomUserDetailsTest {

    private UserInfoDto mockUserInfoDto;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        mockUserInfoDto = mock(UserInfoDto.class);
        customUserDetails = new CustomUserDetails(mockUserInfoDto);
    }

    @Test
    void shouldGetAuthoritiesSuccessfully_givenRoleAdmin() {
        // Role이 "ADMIN"일 때
        when(mockUserInfoDto.getRole()).thenReturn("ADMIN");

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ADMIN");

        verify(mockUserInfoDto, times(1)).getRole();
    }

    @Test
    void shouldGetAuthoritiesSuccessfully_givenNull() {
        when(mockUserInfoDto.getRole()).thenReturn(null);

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("USER");

        verify(mockUserInfoDto, times(1)).getRole();
    }

    @Test
    void shouldGetPasswordSuccessfully() {
        when(mockUserInfoDto.getPassword()).thenReturn("securePassword");

        String password = customUserDetails.getPassword();

        assertThat(password).isEqualTo("securePassword");

        verify(mockUserInfoDto, times(1)).getPassword();
    }

    @Test
    void shouldGetUserNameSuccessfully() {
        when(mockUserInfoDto.getUserId()).thenReturn(123L);

        String username = customUserDetails.getUsername();

        assertThat(username).isEqualTo("123");

        verify(mockUserInfoDto, times(1)).getUserId();
    }

    @Test
    void shouldIsAccountNonLocked_whenLocked() {
        when(mockUserInfoDto.getStatus()).thenReturn(UserStatus.LOCKED.name());

        boolean isNonLocked = customUserDetails.isAccountNonLocked();

        assertThat(isNonLocked).isFalse();

        verify(mockUserInfoDto, times(1)).getStatus();
    }

    @Test
    void testIsAccountNonLocked_whenNotLocked() {
        when(mockUserInfoDto.getStatus()).thenReturn(UserStatus.ACTIVE.name());

        boolean isNonLocked = customUserDetails.isAccountNonLocked();

        assertThat(isNonLocked).isTrue();

        verify(mockUserInfoDto, times(1)).getStatus();
    }

    @Test
    void testIsCredentialsNonExpired_whenNotExpired() {
        when(mockUserInfoDto.getPasswordLastModifiedDate()).thenReturn(LocalDateTime.now().minusDays(30));

        boolean isNonExpired = customUserDetails.isCredentialsNonExpired();

        assertThat(isNonExpired).isTrue();

        verify(mockUserInfoDto, times(1)).getPasswordLastModifiedDate();
    }

    @Test
    void testIsCredentialsNonExpired_whenExpired() {
        when(mockUserInfoDto.getPasswordLastModifiedDate()).thenReturn(LocalDateTime.now().minusDays(91));

        boolean isNonExpired = customUserDetails.isCredentialsNonExpired();

        assertThat(isNonExpired).isFalse();

        verify(mockUserInfoDto, times(1)).getPasswordLastModifiedDate();
    }

    @Test
    void testIsEnabled_whenActive() {
        when(mockUserInfoDto.getStatus()).thenReturn(UserStatus.ACTIVE.name());

        boolean isEnabled = customUserDetails.isEnabled();

        assertThat(isEnabled).isTrue();

        verify(mockUserInfoDto, times(1)).getStatus();
    }

    @Test
    void testIsEnabled_whenInactive() {
        when(mockUserInfoDto.getStatus()).thenReturn(UserStatus.LOCKED.name());

        boolean isEnabled = customUserDetails.isEnabled();

        assertThat(isEnabled).isFalse();

        verify(mockUserInfoDto, times(1)).getStatus();
    }
}