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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final QuestionTagService questionTagService;

    public Tag buildTag(String name) {
        return Tag.builder()
                .name(name)
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

        Set<String> existingTags = tagRepository.findByNames(tagNames).stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream()
                .filter(name -> {
                    if(existingTags.contains(name)) {
                        throw new ApplicationException(ErrorCode.TAG_ALREADY_EXISTS, "Tag already exists: " + name);
                    }
                    return true;
                })
                .map(this::buildTag)
                .toList();
        List<Map<String, Object>> generatedKeys = tagRepository.saveAll(newTags);

        return IntStream.range(0, newTags.size())
                .mapToObj(i -> {
                    newTags.get(i).setKeys(generatedKeys.get(i));
                    return newTags.get(i);
                }).toList();
    }

    public List<String> getStringUpdateTags(TagRequestDto tagRequestDto) {
        return toStringTags(updateTags(tagRequestDto));
    }

    @Transactional
    public List<Tag> updateTags(TagRequestDto tagRequestDto) {
        List<String> tagNames = tagRequestDto.getNames();

        List<Tag> updatedTags = new ArrayList<>();

        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = buildTag(tagName);
                        newTag.setKeys(tagRepository.insertAndReturnGeneratedKeys(newTag));
                        return newTag;
                    });
            updatedTags.add(tag);
        }

        return updatedTags;
    }

    public List<Long> getOrCreateTagIds(TagRequestDto tagRequestDto) {
        List<Long> tagIds = new ArrayList<>();

        for (String tagName : tagRequestDto.getNames()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = buildTag(tagName);
                        newTag.setKeys(tagRepository.insertAndReturnGeneratedKeys(buildTag(tagName)));
                        return newTag;
                    });
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
        return tagRepository.findByIds(tagIds);
    }

    public List<Long> getQuestionIdsByTagName(String tagName) {
        Tag tag = tagRepository.findByName(tagName)
                .orElseThrow(()-> new ApplicationException(ErrorCode.TAG_NOT_FOUND));
        return questionTagService.getQuestionIdsByTagId(tag.getId());
    }

    @Transactional(readOnly = true)
    public List<TagResponseDto> getRecommendedTags(String keyword, int page, int size) {
        List<Tag> tags = tagRepository.searchByName(keyword, page, size);
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