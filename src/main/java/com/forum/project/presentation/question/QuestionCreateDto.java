package com.forum.project.presentation.question;

import com.forum.project.domain.question.Question;
import com.forum.project.domain.user.User;
import com.forum.project.presentation.tag.TagRequestDto;
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
