package com.forum.project.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.domain.auth.dto.EmailVerificationConfirmDto;
import com.forum.project.domain.auth.dto.EmailVerificationRequestDto;
import com.forum.project.domain.auth.service.EmailVerificationService;
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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailVerificationController.class)
@Import(TestSecurityConfig.class)
class EmailVerificationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailVerificationService emailVerificationService;

    private static final String BASE_PATH = "/api/v1/auth/email-verification";
    private EmailVerificationRequestDto emailVerificationRequestDto;
    private EmailVerificationConfirmDto emailVerificationConfirmDto;

    @BeforeEach
    void setUp() {
        emailVerificationRequestDto = TestDtoFactory.createEmailVerificationRequestDto();
        emailVerificationConfirmDto = TestDtoFactory.createEmailVerificationConfirmDto();
    }

    private void testRequestWithValidDto(Object dto, String endpoint) throws Exception {
        mockMvc.perform(post(BASE_PATH + endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void requestEmailVerification() throws Exception {
        testRequestWithValidDto(emailVerificationRequestDto, "/request");

        verify(emailVerificationService).sendVerificationCode(emailVerificationRequestDto.getEmail());
    }

    @Test
    void confirmEmailVerification() throws Exception {
        testRequestWithValidDto(emailVerificationConfirmDto, "/confirm");

        verify(emailVerificationService).verifyEmailCode(
                emailVerificationConfirmDto.getEmail(), emailVerificationConfirmDto.getCode());
    }
}