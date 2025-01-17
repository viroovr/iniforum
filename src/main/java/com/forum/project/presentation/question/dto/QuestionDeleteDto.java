package com.forum.project.presentation.question.dto;

import com.forum.project.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDeleteDto {
    private Long questionId;
    private User user;
}
