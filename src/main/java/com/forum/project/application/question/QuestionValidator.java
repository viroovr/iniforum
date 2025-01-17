package com.forum.project.application.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@RequiredArgsConstructor
public class QuestionValidator {
    private final QuestionRepository questionRepository;

    public void validateOwnership(Long userId, Long questionUserId) {
        if(!questionUserId.equals(userId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
    }

    public void existsQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId))
            throw new ApplicationException(ErrorCode.QUESTION_NOT_FOUND);
    }
}
