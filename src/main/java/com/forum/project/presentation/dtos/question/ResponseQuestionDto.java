package com.forum.project.presentation.dtos.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseQuestionDto {
    private Long id;
    private String title;
    private String loginId;
    private String content;
    private LocalDateTime createdDate;
}
