package com.forum.project.domain.question.dto;

import com.forum.project.domain.user.entity.User;
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
