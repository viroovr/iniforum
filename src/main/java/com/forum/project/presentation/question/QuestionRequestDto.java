package com.forum.project.presentation.question;

import com.forum.project.presentation.tag.TagRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionRequestDto {
    @NotNull
    private String title;
    private String content;
    private TagRequestDto tagRequestDto;
}
