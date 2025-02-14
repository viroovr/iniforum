package com.forum.project.domain.question.service;

import com.forum.project.domain.tag.service.TagService;
import com.forum.project.domain.question.entity.Question;
import com.forum.project.domain.question.mapper.QuestionDtoFactory;
import com.forum.project.domain.question.repository.QuestionRepository;
import com.forum.project.domain.question.vo.QuestionSortType;
import com.forum.project.domain.question.vo.QuestionStatus;
import com.forum.project.domain.question.dto.QuestionPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionQueryService {
    private final QuestionRepository questionRepository;
    private final TagService tagService;

    private Page<QuestionPageResponseDto> getPaginatedResponse(
            Supplier<List<Question>> questionSupplier, Pageable pageable, Supplier<Long> longSupplier
    ) {
        List<QuestionPageResponseDto> response = questionSupplier.get().stream()
                .map(question -> QuestionDtoFactory.toResponsePageDto(
                        question,
                        tagService.getStringTagsByQuestionId(question.getId())))
                .collect(Collectors.toList());
        Long total = longSupplier.get();
        return new PageImpl<>(response, pageable, total);
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByPage(int page, int size) {
        return getPaginatedResponse(
                () -> questionRepository.getByPage(page, size),
                PageRequest.of(page, size),
                questionRepository::countAll);
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsSortedBy(QuestionSortType sortType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sortType.getSort());
        return getPaginatedResponse(
                () -> questionRepository.getByPageable(pageable),
                pageable,
                questionRepository::countAll);
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByKeyword(String keyword, int page, int size) {
        return getPaginatedResponse(
                () -> questionRepository.searchByTitle(keyword, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByTitleKeyword(keyword));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByStatus(QuestionStatus questionStatus, int page, int size) {
        String status = questionStatus.name();
        return getPaginatedResponse(
                () -> questionRepository.findByStatus(status, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByStatus(status));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByUserId(Long userId, int page, int size) {
        return getPaginatedResponse(
                () -> questionRepository.findByUserId(userId, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByUserId(userId));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByTag(String tag, int page, int size) {
        List<Long> questionIds = tagService.getQuestionIdsByTagName(tag);
        return getPaginatedResponse(
                () -> questionRepository.findQuestionByIds(questionIds),
                PageRequest.of(page, size),
                () -> questionRepository.countByQuestionIds(questionIds));
    }
}
