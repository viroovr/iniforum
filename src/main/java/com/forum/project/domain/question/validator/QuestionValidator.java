package com.forum.project.domain.question.validator;

import com.forum.project.core.exception.ApplicationException;
import com.forum.project.core.exception.ErrorCode;
import com.forum.project.domain.question.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionValidator {
    private final QuestionRepository questionRepository;

    public void validateOwnership(Long userId, Long questionUserId) {
        if(!questionUserId.equals(userId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
    }

    public void validateQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId))
            throw new ApplicationException(ErrorCode.QUESTION_NOT_FOUND);
    }
}
