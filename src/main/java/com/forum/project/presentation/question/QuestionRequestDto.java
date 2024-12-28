package com.forum.project.presentation.question;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionRequestDto {

    @NotNull
    private String title;

    private String content;

    private List<String> tag;
}
