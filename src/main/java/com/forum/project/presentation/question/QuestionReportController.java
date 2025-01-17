package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionReportService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.report.ReportRequestDto;
import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions/report")
@RequiredArgsConstructor
public class QuestionReportController {
    private final AuthenticationService authenticationService;
    private final QuestionReportService questionReportService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> reportQuestion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String header,
            @RequestBody ReportRequestDto dto
    ) {
        Long userId = authenticationService.extractUserId(header);
        questionReportService.saveReport(id, userId, dto.getReason());
        BaseResponseDto response = new BaseResponseDto("Question Reported Successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
