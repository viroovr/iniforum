package com.forum.project.presentation.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {
    private String name; // 태그 이름
}
