package com.forum.project.presentation.tag;

import com.forum.project.application.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<TagResponseDto>> getRecommendedTags(@RequestParam String keyword) {
        List<TagResponseDto> recommendedTags = tagService.getRecommendedTags(keyword);
        return ResponseEntity.ok(recommendedTags);
    }

//    @PostMapping
//    public ResponseEntity<TagResponseDto> createTag(@RequestBody TagRequestDto tagRequestDto) {
//        TagResponseDto createdTag = tagService.createTag(tagRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
//    }
}