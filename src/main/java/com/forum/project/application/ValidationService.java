package com.forum.project.application;


import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class ValidationService {
    public <T> void validate(@Valid T object) {
    }
}
