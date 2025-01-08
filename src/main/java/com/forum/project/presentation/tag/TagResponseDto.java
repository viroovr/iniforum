package com.forum.project.presentation.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDto {
    private Long id;     // 태그 ID
    private String name; // 태그 이름
}
