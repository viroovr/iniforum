package com.forum.project.domain.question.dto;

import com.forum.project.domain.user.entity.User;
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
