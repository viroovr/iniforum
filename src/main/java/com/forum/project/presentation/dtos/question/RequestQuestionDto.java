package com.forum.project.presentation.dtos.question;

import com.forum.project.domain.entity.Question;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class RequestQuestionDto {

    @NotNull
    private String title;

    private String content;

    private String tag;

    public static RequestQuestionDto toDto(Question question) {
        return new RequestQuestionDto(
                question.getTitle(),
                question.getContent(),
                question.getTag()
        );
    }
}
