package com.forum.project.presentation.controller;

import com.forum.project.application.io.FileService;
import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.application.user.UserService;
import com.forum.project.presentation.config.TestSecurityConfig;
import com.forum.project.presentation.dtos.user.UserRequestDto;
import com.forum.project.presentation.dtos.user.UserResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

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
    private FileService fileService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserService userService;

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
        when(fileService.uploadFile(any(MultipartFile.class))).thenReturn(uploadDir);
        when(tokenService.extractTokenByHeader(eq(tokenHeader))).thenReturn(extractedToken);
        when(userService.updateUserProfile(eq(extractedToken), any(UserRequestDto.class), eq(uploadDir)))
                .thenReturn(userResponseDto);

        // Perform request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/user/profile")
                        .file(profileImage)
                        .param("nickname", "NewNickname")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value("NewNickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profileImagePath").value(uploadDir));

        // Verify interactions
        Mockito.verify(fileService).uploadFile(any(MultipartFile.class));
        Mockito.verify(tokenService).extractTokenByHeader(eq(tokenHeader));
        Mockito.verify(userService).updateUserProfile(eq(extractedToken), any(UserRequestDto.class), eq(uploadDir));
    }
}
