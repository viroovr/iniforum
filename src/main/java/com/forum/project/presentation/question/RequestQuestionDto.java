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
    @NotNull
    private String userId;
    private String content;
    private String tag;

    public RequestQuestionDto(Long id, String title, String userId, String content, String tag) {
        this.title = title;
        this.userId = userId;
        this.content = content;
        this.tag = tag;
    }

    static public Question toQuestion(RequestQuestionDto requestQuestionDto) {
        return new Question(requestQuestionDto.getTitle(), requestQuestionDto.getUserId(), requestQuestionDto.getContent(),
                requestQuestionDto.getTag());
    }
}
