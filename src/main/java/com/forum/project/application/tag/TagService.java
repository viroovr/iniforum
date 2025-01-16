package com.forum.project.application.tag;

import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.tag.*;
import com.forum.project.presentation.tag.TagRequestDto;
import com.forum.project.presentation.tag.TagResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final QuestionTagService questionTagService;

    public Tag buildTag(String name, String category) {
        return Tag.builder()
                .name(name)
                .category(category)
                .build();
    }

    public List<String> toStringTags(List<Tag> tags) {
        return tags.stream().map(Tag::getName).toList();
    }

    public List<String> getStringTagsByQuestionId(Long questionId) {
        return toStringTags(getTagsByQuestionId(questionId));
    }

    public List<String> createAndAttachTagsToQuestion(TagRequestDto tagRequestDto, Long questionId) {
        List<Tag> tags = createTags(tagRequestDto);
        attachTagsToQuestion(questionId, tags);
        return toStringTags(tags);
    }

    @Transactional
    public List<Tag> createTags(TagRequestDto tagRequestDto) {
        List<String> tagNames = tagRequestDto.getNames();
        TagCategory tagCategory = tagRequestDto.getValidatedCategory();

        List<Tag> newTags = tagNames.stream()
                .map(name -> {
                    if (tagRepository.existsByName(name)) {
                        throw new ApplicationException(ErrorCode.TAG_ALREADY_EXISTS, "Tag already exists: " + name);
                    }
                    return buildTag(name, tagCategory.name());
                })
                .toList();

        return tagRepository.saveAll(newTags);
    }

    public List<String> getStringUpdateTags(TagRequestDto tagRequestDto) {
        return toStringTags(updateTags(tagRequestDto));
    }

    @Transactional
    public List<Tag> updateTags(TagRequestDto tagRequestDto) {
        List<String> tagNames = tagRequestDto.getNames();
        TagCategory tagCategory = tagRequestDto.getValidatedCategory();

        List<Tag> updatedTags = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = buildTag(tagName, tagCategory.name());
                        return tagRepository.save(newTag);
                    });
            updatedTags.add(tag);
        }

        return updatedTags;
    }

    public List<Long> getOrCreateTagIds(TagRequestDto tagRequestDto) {
        List<Long> tagIds = new ArrayList<>();
        TagCategory validatedCategory = tagRequestDto.getValidatedCategory();

        for (String tagName : tagRequestDto.getNames()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(buildTag(tagName, validatedCategory.name())));
            tagIds.add(tag.getId());
        }

        return tagIds;
    }

    private void validateTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty())
            throw new ApplicationException(ErrorCode.INVALID_REQUEST,
                    "Tag IDs must not be empty.");
    }

    public List<Tag> getTagsByQuestionId(Long questionId) {
        List<Long> tagIds = questionTagService.getTagIdsByQuestionId(questionId);
        return tagRepository.findAllById(tagIds);
    }

    public List<Long> getQuestionIdsByTagName(String tagName) {
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(()-> new ApplicationException(ErrorCode.TAG_NOT_FOUND));
        return questionTagService.getQuestionIdsByTagId(tag.getId());
    }

    @Transactional(readOnly = true)
    public List<TagResponseDto> getRecommendedTags(String keyword) {
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase(keyword);
        return tags.stream()
                .map(TagResponseDto::new)
                .toList();
    }

    public long countQuestionsWithTags() {
        return 0;
    }

    public void attachTagsToQuestion(Long id, List<Tag> createdTags) {
        questionTagService.saveQuestionTag(id, createdTags);
    }
}