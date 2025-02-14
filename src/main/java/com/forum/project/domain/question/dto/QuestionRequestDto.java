package com.forum.project.domain.question.dto;

import com.forum.project.domain.tag.dto.TagRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
