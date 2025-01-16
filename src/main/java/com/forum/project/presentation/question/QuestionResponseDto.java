package com.forum.project.presentation.question;

import com.forum.project.presentation.dtos.BaseResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponseDto extends BaseResponseDto {
    private Long questionId;
    private String title;
    private String loginId;
    private String content;
    private LocalDateTime createdDate;
    private List<String> tags;
    private Long upVotedCount = 0L;
}
