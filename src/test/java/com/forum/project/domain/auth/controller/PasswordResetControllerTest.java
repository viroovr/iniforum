package com.forum.project.domain.auth.controller;

import com.forum.project.domain.auth.dto.PasswordResetRequestDto;
import com.forum.project.domain.auth.service.PasswordResetService;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.TestDtoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PasswordResetController.class)
@Import(TestSecurityConfig.class)
class PasswordResetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    private static final String BASE_PATH = "/api/v1/password-reset";

    private PasswordResetRequestDto passwordResetRequestDto;

    @BeforeEach
    void setUp() {
        passwordResetRequestDto = TestDtoFactory.createPasswordResetRequestDto();
    }

    @Test
    void requestPasswordReset() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/request")
                        .param("email", passwordResetRequestDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(passwordResetService).sendNewResetTokenToEmail(passwordResetRequestDto.getEmail());
    }

    @Test
    void resetPassword() throws Exception {
        mockMvc.perform(post(BASE_PATH + "/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(passwordResetRequestDto)))
                .andExpect(status().isOk());

        verify(passwordResetService).resetPassword(passwordResetRequestDto);
    }
}