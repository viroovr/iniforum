package com.forum.project.application.question;

import com.forum.project.application.bookmark.QuestionBookmarkService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.report.ReportRequestDto;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.QuestionStatus;
import com.forum.project.presentation.question.dto.QuestionPageResponseDto;
import com.forum.project.presentation.question.dto.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionCrudService questionCrudService;
    private final AuthenticationService authenticationService;
    private final QuestionReportService questionReportService;
    private final QuestionBookmarkService questionBookmarkService;
    private final QuestionLikeService questionLikeService;

    public QuestionResponseDto voteUpQuestion(Long questionId, String token) {
        Long userId = authenticationService.extractUserId(token);
        questionLikeService.addLike(questionId, userId);
        return questionCrudService.updateUpVotedCount(questionId, userId);
    }

    public Page<QuestionPageResponseDto> getQuestionsByPage(int page, int size) {
        return questionCrudService.readQuestionsByPage(page, size);
    }

    public Page<QuestionPageResponseDto> getQuestionsByKeyword(String keyword, int page, int size) {
        return questionCrudService.readQuestionsByKeyword(keyword, page, size);
    }

    public Page<QuestionPageResponseDto> getUnansweredQuestions(int page, int size) {
        return questionCrudService.readQuestionsByStatus(QuestionStatus.OPEN, page, size);
    }

    public Page<QuestionPageResponseDto> getQuestionsByUser(Long userId, int page, int size) {
        return questionCrudService.readQuestionsByUserId(userId, page, size);
    }

    public Page<QuestionPageResponseDto> getQuestionsByTag(String tag, int page, int size) {
        return questionCrudService.readQuestionsByTag(tag, page, size);
    }

    public Page<QuestionPageResponseDto> getQuestionsSortedBy(String sortBy, int page, int size) {
        QuestionSortType sortType = QuestionSortType.fromString(sortBy);
        return questionCrudService.readQuestionsSortedBy(sortType, page, size);
    }

    public void reportQuestion(Long questionId, ReportRequestDto dto, String token) {
        Long userId = authenticationService.extractUserId(token);
        if (questionCrudService.existsQuestion(questionId))
            throw new ApplicationException(ErrorCode.QUESTION_NOT_FOUND);

        questionReportService.saveReport(questionId, userId, dto.getReason());
    }

    public void bookmarkQuestion(Long questionId, String token) {
        Long userId = authenticationService.extractUserId(token);
        questionBookmarkService.bookmarkQuestion(questionId, userId);
    }
}
