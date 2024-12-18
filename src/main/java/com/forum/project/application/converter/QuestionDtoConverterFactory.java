package com.forum.project.application.converter;

import com.forum.project.domain.entity.Question;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import org.springframework.stereotype.Component;

@Component
public class QuestionDtoConverterFactory {

    public static RequestQuestionDto toRequestQuestionDto(Question question) {
        return RequestQuestionDto.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .build();
    }

   public ResponseQuestionDto toResponseQuestionDto(Question question) {
        return ResponseQuestionDto.builder()
                .title(question.getTitle())
                .loginId(question.getLoginId())
                .content(question.getContent())
                .createdDate(question.getCreatedDate())
                .build();
    }
}
