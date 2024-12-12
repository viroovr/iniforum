package com.forum.project.presentation.dtos.question;

import com.forum.project.domain.entity.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionDtoConverter {

    public RequestQuestionDto toRequestQuestionDto(Question question) {
        return new RequestQuestionDto(
                question.getTitle(),
                question.getContent(),
                question.getTag()
        );
    }

   public ResponseQuestionDto toResponseQuestionDto(Question question) {
        return new ResponseQuestionDto(
                question.getId(),
                question.getTitle(),
                question.getUserId(),
                question.getContent(),
                question.getTag(),
                question.getCreatedDate()
        );
    }
}
