package com.forum.project.presentation.config;

import com.forum.project.application.converter.CommentDtoConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
public class TestConfig {
    public CommentDtoConverter converter() {
        return new CommentDtoConverter();
    }
}
