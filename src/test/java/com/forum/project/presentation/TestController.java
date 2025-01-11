package com.forum.project.presentation;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/trigger-error")
    public String triggerError() {
        throw new ApplicationException(ErrorCode.INVALID_REQUEST, "userId is invalid form");
    }
}
