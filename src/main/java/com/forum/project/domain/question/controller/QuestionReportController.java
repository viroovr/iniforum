package com.forum.project.domain.question.controller;

import com.forum.project.domain.report.service.QuestionReportService;
import com.forum.project.domain.auth.service.AuthorizationService;
import com.forum.project.domain.report.dto.ReportRequestDto;
import com.forum.project.domain.report.entity.QuestionReport;
import com.forum.project.core.base.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions/report")
@RequiredArgsConstructor
public class QuestionReportController {
    private final AuthorizationService authorizationService;
    private final QuestionReportService questionReportService;

    @PostMapping(value = "/{id}")
    public ResponseEntity<BaseResponseDto> saveQuestionReport(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader("Authorization") String header,
            @RequestBody ReportRequestDto dto
    ) {
        Long userId = authorizationService.extractUserId(header);
        questionReportService.saveReport(questionId, userId, dto.getReason());
        BaseResponseDto response = new BaseResponseDto("Question Reported Successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<List<QuestionReport>> getQuestionReport(
            @PathVariable(value = "id") Long questionId,
            @RequestHeader("Authorization") String header
    ) {
        authorizationService.validateAdminRole(header);
        List<QuestionReport> response = questionReportService.getReportsById(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<QuestionReport>> getQuestionReportByUser(
            @PathVariable(value = "id") Long userId,
            @RequestHeader("Authorization") String header
    ) {
        authorizationService.validateUser(userId, header);
        List<QuestionReport> response = questionReportService.getReportsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/resolve/{id}")
    public ResponseEntity<BaseResponseDto> resolveQuestionReport(
            @PathVariable(value = "id") Long reportId,
            @RequestHeader("Authorization") String header
    ) {
        authorizationService.validateAdminRole(header);
        questionReportService.resolveReport(reportId);
        BaseResponseDto response = new BaseResponseDto("Question Resolved Successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
