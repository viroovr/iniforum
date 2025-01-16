package com.forum.project.application.question;

import com.forum.project.domain.question.Question;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.question.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionDtoFactory {

    public static Question toEntity(QuestionCreateDto dto) {
        return Question.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .loginId(dto.getUser().getLoginId())
                .userId(dto.getUser().getId())
                .build();
    }

    public static QuestionResponseDto toResponseDto(Question q, List<String> tag) {
        return QuestionResponseDto.builder()
                .questionId(q.getId())
                .title(q.getTitle())
                .content(q.getContent())
                .createdDate(q.getCreatedDate())
                .tags(tag)
                .build();
    }

    public static QuestionPageResponseDto toResponsePageDto(Question q, List<String> tag) {
        return QuestionPageResponseDto.builder()
                .questionId(q.getId())
                .title(q.getTitle())
                .loginId(q.getLoginId())
                .createdDate(q.getCreatedDate())
                .tags(tag)
                .build();
    }

    public static QuestionCreateDto toCreateDto(QuestionRequestDto dto, User user) {
        return QuestionCreateDto.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .tagRequestDto(dto.getTagRequestDto())
                .user(user)
                .build();
    }

    public static QuestionUpdateDto toUpdateDto(QuestionRequestDto dto, Long questionId, User user) {
        return QuestionUpdateDto.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .tagRequestDto(dto.getTagRequestDto())
                .questionId(questionId)
                .user(user)
                .build();
    }
}
