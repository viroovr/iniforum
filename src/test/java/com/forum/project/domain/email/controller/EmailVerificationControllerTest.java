package com.forum.project.domain.email.controller;

import com.forum.project.domain.auth.dto.EmailVerificationConfirmDto;
import com.forum.project.domain.auth.dto.EmailVerificationRequestDto;
import com.forum.project.domain.email.service.EmailVerificationService;
import com.forum.project.presentation.dtos.TestDtoFactory;
import com.forum.project.testUtils.CustomMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CustomMockMvc.class)
@WebMvcTest(controllers = EmailVerificationController.class)
class EmailVerificationControllerTest {
    @Autowired
    private CustomMockMvc customMockMvc;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private static final String BASE_PATH = "/api/v1/auth/email-verification";
    private EmailVerificationRequestDto requestDto;
    private EmailVerificationConfirmDto confirmDto;

    @BeforeEach
    void setUp() {
        requestDto = TestDtoFactory.createEmailVerificationRequestDto();
        confirmDto = TestDtoFactory.createEmailVerificationConfirmDto();
    }

    @Test
    void requestEmailVerification() throws Exception {
        customMockMvc.postRequest(BASE_PATH + "/request", requestDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(emailVerificationService).sendVerificationCode(requestDto.getEmail());
    }

    @Test
    void confirmEmailVerification() throws Exception {
        customMockMvc.postRequest(BASE_PATH + "/confirm", confirmDto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(emailVerificationService).verifyEmailCode(confirmDto.getEmail(), confirmDto.getCode());
    }
}