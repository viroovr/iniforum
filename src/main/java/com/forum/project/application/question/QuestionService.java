package com.forum.project.application.question;

import com.forum.project.application.jwt.TokenService;
import com.forum.project.application.user.auth.AuthenticationService;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.tag.QuestionTag;
import com.forum.project.domain.tag.Tag;
import com.forum.project.domain.user.User;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.domain.question.TotalCountRepository;
import com.forum.project.infrastructure.persistence.tag.QuestionTagRepository;
import com.forum.project.infrastructure.persistence.tag.TagRepository;
import com.forum.project.infrastructure.persistence.user.UserRepository;
import com.forum.project.presentation.question.QuestionRequestDto;
import com.forum.project.presentation.question.QuestionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TotalCountRepository totalCountRepository;
    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    private Question saveQuestion(Question question) {
        Question savedQuestion = questionRepository.save(question);
        totalCountRepository.incrementTotalCount();
        return savedQuestion;
    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionRequestDto, String header) {
        User user = authenticationService.extractUserByHeader(header);

        Question question = Question.builder()
                .title(questionRequestDto.getTitle())
                .userId(user.getId())
                .content(questionRequestDto.getContent())
                .build();
        Question savedQuestion = saveQuestion(question);

        // 태그 연결
        List<Long> tagIds = questionRequestDto.getTagIds();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.isEmpty()) {
                throw new ApplicationException(ErrorCode.TAG_NOT_FOUND);
            }

            // QuestionTag 저장
            tags.forEach(tag -> {
                QuestionTag questionTag = new QuestionTag(savedQuestion.getId(), tag.getId());
                questionTagRepository.save(questionTag);
            });
        }

        return QuestionDtoConverterFactory.toResponseQuestionDto(savedQuestion);
    }

    public QuestionResponseDto findById(Long id) {
        Question question = questionRepository
                .findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
        // 연결된 태그 조회
        List<Tag> tags = tagRepository.findTagsByQuestionId(id);

        // 질문 DTO로 변환
        QuestionResponseDto responseDto = QuestionDtoConverterFactory.toResponseQuestionDto(question);
        responseDto.setTags(tags.stream().map(Tag::getName).toList()); // 태그 이름만 포함
        return QuestionDtoConverterFactory.toResponseQuestionDto(question);
    }

    private Question validateQuestion(Long questionId, String token) {
        String currentUserId = tokenService.getLoginId(token);
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));

        if(!question.getLoginId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.AUTH_BAD_CREDENTIAL);
        }
        return question;
    }

    @Transactional
    public void deleteQuestion(Long questionId, String accessToken) {
        validateQuestion(questionId, accessToken);
        questionRepository.deleteById(questionId);
        totalCountRepository.decrementTotalCount();
    }

    @Transactional
    public QuestionResponseDto updateQuestion(Long questionId, QuestionRequestDto questionRequestDto, String accessToken) {
        Question question = validateQuestion(questionId, accessToken);

        question.setTitle(questionRequestDto.getTitle());
        question.setContent(questionRequestDto.getContent());

        return QuestionDtoConverterFactory.toResponseQuestionDto(questionRepository.save(question));
    }

    private Long getTotalQuestionCount() {
        Long total = totalCountRepository.getTotalCount();
        if (total == null) {
            total = questionRepository.count();
            totalCountRepository.setTotalCount(total);
        }
        return total;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<QuestionResponseDto> getQuestionsByPage(int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.getQuestionByPage(page, size);

        List<QuestionResponseDto> questionResponseDtos =
                questionPage.stream().map(QuestionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<QuestionResponseDto> searchQuestions(String keyword, int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.searchQuestions(keyword, page, size);

        List<QuestionResponseDto> questionResponseDtos =
                questionPage.stream().map(QuestionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }

    public Page<QuestionResponseDto> getQuestionsByUser(int page, int size, String accessToken) {
        Long id = tokenService.getId(accessToken);
        Long total = questionRepository.getTotalUserQuestionCount(id);
        List<Question> questionPage = questionRepository.searchQuestionsByUser(id, page, size);

        List<QuestionResponseDto> questionResponseDtos =
                questionPage.stream().map(QuestionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }

    @Transactional
    public void upvoteQuestion(Long questionId, String accessToken) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));

        question.incrementUpVotedCount();
        questionRepository.save(question);
    }

//    @Transactional(readOnly = true)
//    public Page<QuestionResponseDto> getQuestionsByTag(String tag, int page, int size) {
//        List<Question> questionPage = questionRepository.findQuestionsByTag(tag, page, size);
//
//        List<QuestionResponseDto> questionResponseDtos =
//                questionPage.stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();
//
//        Pageable pageable = PageRequest.of(page, size);
//        return new PageImpl<>(questionResponseDtos, pageable, (long) questionPage.size());
//    }
//
//    @Transactional(readOnly = true)
//    public Page<QuestionResponseDto> getUnansweredQuestions(int page, int size) {
//        List<Question> unansweredQuestions = questionRepository.findUnansweredQuestions(page, size);
//
//        List<QuestionResponseDto> questionResponseDtos =
//                unansweredQuestions.stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();
//
//        Pageable pageable = PageRequest.of(page, size);
//        return new PageImpl<>(questionResponseDtos, pageable, (long) unansweredQuestions.size());
//    }
//
//    @Transactional
//    public void reportQuestion(Long questionId, String reason, String accessToken) {
//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
//
//        Report report = Report.builder()
//                .questionId(questionId)
//                .reason(reason)
//                .userId(tokenService.getId(accessToken))
//                .build();
//
//        reportRepository.save(report);
//    }
//
//    @Transactional
//    public void bookmarkQuestion(Long questionId, String accessToken) {
//        Long userId = tokenService.getId(accessToken);
//        Bookmark bookmark = Bookmark.builder()
//                .userId(userId)
//                .questionId(questionId)
//                .build();
//
//        bookmarkRepository.save(bookmark);
//    }
//
//    @Transactional
//    public void incrementQuestionViewCount(Long questionId) {
//        Question question = questionRepository.findById(questionId)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
//
//        question.incrementViewCount();
//        questionRepository.save(question);
//    }
//
//    @Transactional(readOnly = true)
//    public Map<String, Long> getQuestionStatistics() {
//        long totalQuestions = questionRepository.count();
//        long unansweredQuestions = questionRepository.countUnansweredQuestions();
//        long taggedQuestions = tagRepository.countQuestionsWithTags();
//
//        return Map.of(
//                "totalQuestions", totalQuestions,
//                "unansweredQuestions", unansweredQuestions,
//                "taggedQuestions", taggedQuestions
//        );
//    }
//
//    @Transactional(readOnly = true)
//    public Page<QuestionResponseDto> getQuestionsSortedBy(String sortBy, int page, int size) {
//        Sort sort = switch (sortBy) {
//            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
//            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
//            case "votes" -> Sort.by(Sort.Direction.DESC, "upvoteCount");
//            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
//            default -> throw new IllegalArgumentException("Invalid sort option");
//        };
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<Question> questionPage = questionRepository.findAll(pageable);
//
//        List<QuestionResponseDto> questionResponseDtos =
//                questionPage.getContent().stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();
//
//        return new PageImpl<>(questionResponseDtos, pageable, questionPage.getTotalElements());
//    }
}
