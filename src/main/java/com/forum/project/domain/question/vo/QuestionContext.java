package com.forum.project.domain.question.vo;

import lombok.Builder;

@Builder
public record QuestionContext(Long questionId, Long userId) {
}
