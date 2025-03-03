package com.forum.project.presentation;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/trigger-error")
    public String triggerError() {
        throw new ApplicationException(ErrorCode.INVALID_REQUEST, "userId is invalid form");
    }
}
