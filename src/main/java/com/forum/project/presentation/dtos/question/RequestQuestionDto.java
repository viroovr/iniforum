package com.forum.project.presentation.dtos.question;

import com.forum.project.domain.entity.Question;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestQuestionDto {

    @NotNull
    private String title;

    private String content;

    private List<String> tag;
}
