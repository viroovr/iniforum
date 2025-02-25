package com.forum.project.testUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forum.project.domain.auth.aspect.IpAddressAspect;
import com.forum.project.domain.auth.aspect.UserIdAspect;
import com.forum.project.domain.auth.service.TokenService;
import com.forum.project.presentation.config.TestSecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@TestComponent
@ActiveProfiles("test")
@EnableAspectJAutoProxy
@RequiredArgsConstructor
@Import({UserIdAspect.class, IpAddressAspect.class, TestSecurityConfig.class})
public class CustomMockMvc {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private TokenService tokenService;

    public static final String AUTHORIZATION_HEADER = "Bearer token";

    private void setUp() {
        when(tokenService.getUserId("token")).thenReturn(1L);
    }

    public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
        setUp();
        return mockMvc.perform(requestBuilder);
    }

    public MockHttpServletRequestBuilder requestBuilder(MockHttpServletRequestBuilder builder, Object dto) throws JsonProcessingException {
        setUp();
        return builder
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", AUTHORIZATION_HEADER)
                .content(objectMapper.writeValueAsString(dto));
    }

    public ResultActions postRequest(String url, Object dto, Object... uriVars) throws Exception {
        return mockMvc.perform(requestBuilder(post(url, uriVars), dto));
    }

    public ResultActions putRequestWithoutDto(String url, Object... uriVars) throws Exception {
        setUp();
        return mockMvc.perform(put(url, uriVars)
                .header("Authorization", AUTHORIZATION_HEADER));
    }

    public ResultActions deleteRequestWithoutDto(String url, Object... uriVars) throws Exception {
        setUp();
        return mockMvc.perform(delete(url, uriVars)
                .header("Authorization", AUTHORIZATION_HEADER));
    }

    public ResultActions getRequest(String url, Object... uriVars) throws Exception {
        setUp();
        return mockMvc.perform(get(url, uriVars)
                .header("Authorization", AUTHORIZATION_HEADER));
    }

    public ResultActions putRequest(String url, Object dto, Object... uriVars) throws Exception {
        return mockMvc.perform(requestBuilder(put(url, uriVars), dto));
    }

    public ResultActions deleteRequest(String url, Object dto, Object... uriVars) throws Exception {
        return mockMvc.perform(requestBuilder(delete(url, uriVars), dto));
    }
}
