package com.forum.project.presentation.dtos.question;



import com.forum.project.domain.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResponseQuestionDto {
    private Long id;
    private String title;
    private String userId;
    private String content;
    private String tag;
    private LocalDateTime createdDate;


    static public ResponseQuestionDto toDto(Question question) {
        return new ResponseQuestionDto(question.getId(), question.getTitle(), question.getUserId(), question.getContent(),
                question.getTag(), question.getCreatedDate());
    }

}
