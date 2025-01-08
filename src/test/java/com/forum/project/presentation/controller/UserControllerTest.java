package com.forum.project.presentation.controller;

import com.forum.project.application.jwt.TokenService;
import com.forum.project.application.user.UserFacade;
import com.forum.project.application.question.QuestionService;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.user.UserRequestDto;
import com.forum.project.presentation.user.UserResponseDto;
import com.forum.project.presentation.user.UserController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private QuestionService questionService;

    private final UserRequestDto userRequestDto = new UserRequestDto("oldPassword", "newPassword", "newNickname");
    private final UserResponseDto userResponseDto = new UserResponseDto(null, "test/upload/path", "newNickname");

    @Test
    @WithMockUser
    void testUpdateUserProfile_Success() throws Exception {
        // Mock input values
        String tokenHeader = "Bearer mock-token";
        String extractedToken = "mock-token";
        String uploadDir = "test/upload/path";

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "mock image content".getBytes()
        );

        // Mocking service calls
        when(userFacade.updateUserProfileByHeader(eq(tokenHeader), any(UserRequestDto.class), eq(profileImage)))
                .thenReturn(userResponseDto);

        // Perform request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/user/profile")
                        .file(profileImage)
                        .param("nickname", "newNickname")
                        .param("password", "oldPassword")
                        .param("newPassword", "newPassword")
                        .header("Authorization", tokenHeader)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT"); // HTTP 메서드를 PUT으로 설정
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("newNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profileImagePath").value(uploadDir));

        // Verify interactions
        Mockito.verify(userFacade).updateUserProfileByHeader(eq(extractedToken), any(UserRequestDto.class), eq(profileImage));
    }
}
