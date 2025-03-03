package com.forum.project.domain.question.mapper;

import com.forum.project.domain.question.dto.*;
import com.forum.project.domain.question.entity.Question;
import com.forum.project.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionDtoFactory {

    public static Question toEntity(QuestionCreateDto dto) {
        return Question.builder()
                .content(dto.getContent())
                .title(dto.getTitle())
                .userId(dto.getUser().getId())
                .build();
    }

    public static QuestionResponseDto toResponseDto(Question q, List<String> tag, Long viewCount) {
        return QuestionResponseDto.builder()
                .questionId(q.getId())
                .title(q.getTitle())
                .content(q.getContent())
                .createdDate(q.getCreatedDate())
                .tags(tag)
                .viewCount(viewCount)
                .build();
    }

    public static QuestionResponseDto toResponseDto(Question q) {
        return QuestionResponseDto.builder()
                .questionId(q.getId())
                .title(q.getTitle())
                .content(q.getContent())
                .createdDate(q.getCreatedDate())
                .build();
    }

    public static QuestionPageResponseDto toResponsePageDto(Question q, List<String> tag) {
        return QuestionPageResponseDto.builder()
                .questionId(q.getId())
                .title(q.getTitle())
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

    public static QuestionUpdateDto toUpdateDto(QuestionRequestDto dto, Long questionId, Long userId) {
        return QuestionUpdateDto.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .tagRequestDto(dto.getTagRequestDto())
                .questionId(questionId)
                .userId(userId)
                .build();
    }
}
