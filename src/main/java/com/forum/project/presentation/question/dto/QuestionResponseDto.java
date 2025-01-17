package com.forum.project.presentation.question.dto;

import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionResponseDto extends BaseResponseDto {
    private Long questionId;
    private String title;
    private String loginId;
    private String content;
    private LocalDateTime createdDate;
    private List<String> tags;
    private Long viewCount;
    private Long upVotedCount;
}
