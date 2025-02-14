package com.forum.project.domain.tag.controller;

import com.forum.project.domain.tag.service.TagService;
import com.forum.project.domain.tag.dto.TagResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/recommendations")
    public ResponseEntity<List<TagResponseDto>> getRecommendedTags(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size
    ) {
        List<TagResponseDto> recommendedTags = tagService.getRecommendedTags(keyword, page, size);
        return ResponseEntity.ok(recommendedTags);
    }

//    @PostMapping
//    public ResponseEntity<TagResponseDto> createTag(@RequestBody TagRequestDto tagRequestDto) {
//        TagResponseDto createdTag = tagService.createTag(tagRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
//    }
}