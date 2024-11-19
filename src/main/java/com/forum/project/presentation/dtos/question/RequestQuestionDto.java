package com.forum.project.presentation.dtos.question;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class RequestQuestionDto {
    @NotNull
    private String title;
    private String content;
    private String tag;

    public RequestQuestionDto(String title, String content, String tag) {
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

}
