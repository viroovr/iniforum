package com.forum.project.application.question;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class QuestionValidator {

    public void validateOwnership(Long userId, Long questionUserId) {
        if(!questionUserId.equals(userId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
    }
}
