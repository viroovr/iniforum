package com.forum.project.presentation.question;

import com.forum.project.domain.user.User;
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
    private User user;
}
