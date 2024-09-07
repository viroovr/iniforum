package com.forum.project.presentation.question;

import com.forum.project.domain.Question;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

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
