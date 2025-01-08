package com.forum.project.domain.commentlike;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReportRequestDto {
    private String reason;
}
