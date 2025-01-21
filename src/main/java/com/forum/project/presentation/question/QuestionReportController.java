package com.forum.project.presentation.question;

import com.forum.project.application.question.QuestionReportService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.report.ReportRequestDto;
import com.forum.project.domain.report.question.QuestionReport;
import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions/report")
@RequiredArgsConstructor
public class QuestionReportController {
    private final AuthenticationService authenticationService;
    private final QuestionReportService questionReportService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> saveQuestionReport(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader("Authorization") String header,
            @RequestBody ReportRequestDto dto
    ) {
        Long userId = authenticationService.extractUserId(header);
        questionReportService.saveReport(questionId, userId, dto.getReason());
        BaseResponseDto response = new BaseResponseDto("Question Reported Successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<QuestionReport>> getQuestionReport(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader("Authorization") String header
    ) {
        authenticationService.validateAdminRole(header);
        List<QuestionReport> response = questionReportService.getReportsById(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<QuestionReport>> getQuestionReportByUser(
            @PathVariable(value = "id") Long userId,
            @RequestHeader("Authorization") String header
    ) {
        authenticationService.validateUser(userId, header);
        List<QuestionReport> response = questionReportService.getReportsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/resolve/{id}")
    public ResponseEntity<BaseResponseDto> resolveQuestionReport(
            @PathVariable(value = "id") Long reportId,
            @RequestHeader("Authorization") String header
    ) {
        authenticationService.validateAdminRole(header);
        questionReportService.resolveReport(reportId);
        BaseResponseDto response = new BaseResponseDto("Question Resolved Successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
