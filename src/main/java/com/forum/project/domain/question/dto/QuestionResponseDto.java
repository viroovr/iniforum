package com.forum.project.domain.question.dto;

import com.forum.project.core.base.BaseResponseDto;
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
