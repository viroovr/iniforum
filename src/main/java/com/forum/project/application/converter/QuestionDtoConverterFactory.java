package com.forum.project.application.converter;

import com.forum.project.domain.question.Question;
import com.forum.project.presentation.question.QuestionRequestDto;
import com.forum.project.presentation.question.QuestionResponseDto;
import org.springframework.stereotype.Component;

@Component
public class QuestionDtoConverterFactory {

    public static QuestionRequestDto toRequestQuestionDto(Question question) {
        return QuestionRequestDto.builder()
                .title(question.getTitle())
                .content(question.getContent())
                .build();
    }

   public QuestionResponseDto toResponseQuestionDto(Question question) {
        return QuestionResponseDto.builder()
                .title(question.getTitle())
                .loginId(question.getLoginId())
                .content(question.getContent())
                .createdDate(question.getCreatedDate())
                .build();
    }
}
