package com.forum.project.application.tag;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.tag.Tag;
import com.forum.project.infrastructure.persistence.tag.TagRepository;
import com.forum.project.presentation.tag.TagRequestDto;
import com.forum.project.presentation.tag.TagResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponseDto> getRecommendedTags(String keyword) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(keyword);
        return tags.stream()
                .map(tag -> new TagResponseDto(tag.getId(), tag.getName()))
                .toList();
    }

    @Transactional
    public TagResponseDto createTag(TagRequestDto tagRequestDto) {
        // 중복 태그 방지
        if (tagRepository.existsByName(tagRequestDto.getName())) {
            throw new ApplicationException(ErrorCode.TAG_ALREADY_EXISTS);
        }

        Tag tag = Tag.builder()
                .name(tagRequestDto.getName())
                .build();
        Tag savedTag = tagRepository.save(tag);

        return new TagResponseDto(savedTag.getId(), savedTag.getName());
    }
}