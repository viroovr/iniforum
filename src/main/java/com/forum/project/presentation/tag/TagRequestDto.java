package com.forum.project.presentation.tag;

import com.forum.project.domain.tag.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {
    private List<String> names;
    private String category;

    public TagCategory getValidatedCategory() {
        try {
            return TagCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }
    }
}
