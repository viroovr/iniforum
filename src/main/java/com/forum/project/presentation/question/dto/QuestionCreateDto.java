package com.forum.project.presentation.question.dto;

import com.forum.project.domain.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionCreateDto extends QuestionRequestDto {
    private User user;
}
