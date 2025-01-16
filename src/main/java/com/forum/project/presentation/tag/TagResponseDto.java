package com.forum.project.presentation.tag;

import com.forum.project.domain.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDto {
    private Long id;
    private String name;
    private String category;

    public TagResponseDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.category = tag.getCategory();
    }
}
