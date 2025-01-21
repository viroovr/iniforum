package com.forum.project.application.question;

import com.forum.project.application.tag.TagService;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.domain.question.QuestionStatus;
import com.forum.project.presentation.question.dto.QuestionPageResponseDto;
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
                () -> questionRepository.getQuestionByPage(page, size),
                PageRequest.of(page, size),
                questionRepository::count);
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsSortedBy(QuestionSortType sortType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, sortType.getSort());
        return getPaginatedResponse(
                () -> questionRepository.findAll(pageable),
                pageable,
                questionRepository::count);
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByKeyword(String keyword, int page, int size) {
        return getPaginatedResponse(
                () -> questionRepository.findQuestionsByKeyword(keyword, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByKeyword(keyword));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByStatus(QuestionStatus questionStatus, int page, int size) {
        String status = questionStatus.name();
        return getPaginatedResponse(
                () -> questionRepository.findQuestionsByStatus(status, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByStatus(status));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByUserId(Long userId, int page, int size) {
        return getPaginatedResponse(
                () -> questionRepository.searchQuestionsByUser(userId, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByUserId(userId));
    }

    @Transactional(readOnly = true)
    public Page<QuestionPageResponseDto> readQuestionsByTag(String tag, int page, int size) {
        List<Long> questionIds = tagService.getQuestionIdsByTagName(tag);
        return getPaginatedResponse(
                () -> questionRepository.searchQuestionsByQuestionIds(questionIds, page, size),
                PageRequest.of(page, size),
                () -> questionRepository.countByQuestionIds(questionIds));
    }
}
