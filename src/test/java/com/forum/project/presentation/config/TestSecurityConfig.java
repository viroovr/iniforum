package com.forum.project.presentation.config;

import com.forum.project.application.user.auth.CustomUserDetailsService;
import com.forum.project.common.utils.ExceptionResponseUtil;
import com.forum.project.infrastructure.config.SecurityConfig;
import com.forum.project.infrastructure.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mockito.Mock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
@Slf4j
public class TestSecurityConfig {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;
    @MockBean
    private ExceptionResponseUtil exceptionResponseUtil;
    @MockBean
    private AuthenticationManager authenticationManager;  // 인증 매니저 Mock

    @MockBean
    private CustomUserDetailsService customUserDetailsService; // 사용자 세부 정보 서비스 Mock
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("test security filter chain start");
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
