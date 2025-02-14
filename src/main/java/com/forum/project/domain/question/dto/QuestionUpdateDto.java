package com.forum.project.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class QuestionUpdateDto extends QuestionRequestDto {
    private Long questionId;
    private Long userId;
}
