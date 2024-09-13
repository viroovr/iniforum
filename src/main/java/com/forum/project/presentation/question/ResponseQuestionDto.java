package com.forum.project.presentation.question;



import com.forum.project.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

    @Setter
    private int postNumber;


    static public ResponseQuestionDto toDto(Question question) {
        return new ResponseQuestionDto(question.getId(), question.getTitle(), question.getUserId(), question.getContent(),
                question.getTag(), question.getCreatedDate(), 0);
    }

}
