package com.forum.project.presentation.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionPageResponseDto {
    private Long questionId;
    private String title;
    private String loginId;
    private LocalDateTime createdDate;
    private List<String> tags;
}
