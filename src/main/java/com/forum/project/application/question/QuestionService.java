package com.forum.project.application.question;

import com.forum.project.application.jwt.TokenService;
import com.forum.project.domain.question.Question;
import com.forum.project.domain.user.User;
import com.forum.project.application.exception.ApplicationException;
import com.forum.project.application.exception.ErrorCode;
import com.forum.project.domain.question.QuestionRepository;
import com.forum.project.domain.TotalCountRepository;
import com.forum.project.domain.user.UserRepository;
import com.forum.project.application.converter.QuestionDtoConverterFactory;
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
    private final UserRepository userRepository;

    private final QuestionDtoConverterFactory questionDtoConverterFactory;
    private final TokenService tokenService;

    private User extractUserFromJwt(String jwt) {
        long id = tokenService.getId(jwt);
        return userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private Question saveQuestion(Question question) {
        Question savedQuestion = questionRepository.save(question);
        totalCountRepository.incrementTotalCount();
        return savedQuestion;
    }

    @Transactional
    public QuestionResponseDto createQuestion(QuestionRequestDto questionRequestDto, String jwt) {
        User user = extractUserFromJwt(jwt);

        Question question = Question.builder()
                .title(questionRequestDto.getTitle())
                .userId(user.getId())
                .content(questionRequestDto.getContent())
                .build();
        Question savedQuestion = saveQuestion(question);
        return questionDtoConverterFactory.toResponseQuestionDto(savedQuestion);
    }

    public QuestionResponseDto findById(Long id) {
        Question question = questionRepository
                .findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
        return questionDtoConverterFactory.toResponseQuestionDto(question);
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

        return questionDtoConverterFactory.toResponseQuestionDto(questionRepository.save(question));
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
                questionPage.stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<QuestionResponseDto> searchQuestions(String keyword, int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.searchQuestions(keyword, page, size);

        List<QuestionResponseDto> questionResponseDtos =
                questionPage.stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }

    public Page<QuestionResponseDto> getQuestionsByUser(int page, int size, String accessToken) {
        Long id = tokenService.getId(accessToken);
        Long total = questionRepository.getTotalUserQuestionCount(id);
        List<Question> questionPage = questionRepository.searchQuestionsByUser(id, page, size);

        List<QuestionResponseDto> questionResponseDtos =
                questionPage.stream().map(questionDtoConverterFactory::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(questionResponseDtos, pageable, total);
    }
}
