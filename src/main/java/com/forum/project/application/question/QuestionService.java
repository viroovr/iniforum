package com.forum.project.application.question;

import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.Question;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.QuestionRepository;
import com.forum.project.domain.repository.TotalCountRepository;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.question.QuestionDtoConverter;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
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

    private final QuestionDtoConverter questionDtoConverter;
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
    public ResponseQuestionDto createQuestion(RequestQuestionDto requestQuestionDto, String jwt) {
        User user = extractUserFromJwt(jwt);

        Question question = Question.builder()
                .title(requestQuestionDto.getTitle())
                .userId(user.getUserId())
                .content(requestQuestionDto.getContent())
                .tag(requestQuestionDto.getTag())
                .build();
        Question savedQuestion = saveQuestion(question);
        return questionDtoConverter.toResponseQuestionDto(savedQuestion);
    }

    public ResponseQuestionDto findById(Long id) {
        Question question = questionRepository
                .findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
        return questionDtoConverter.toResponseQuestionDto(question);
    }

    private Question validateQuestion(Long questionId, String token) {
        String currentUserId = tokenService.getUserId(token);
        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));

        if(!question.getUserId().equals(currentUserId)) {
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
    public ResponseQuestionDto updateQuestion(Long questionId, RequestQuestionDto requestQuestionDto, String accessToken) {
        Question question = validateQuestion(questionId, accessToken);

        question.setTitle(requestQuestionDto.getTitle());
        question.setContent(requestQuestionDto.getContent());
        question.setTag(requestQuestionDto.getTag());

        return questionDtoConverter.toResponseQuestionDto(questionRepository.save(question));
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
    public Page<ResponseQuestionDto> getQuestionsByPage(int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.getQuestionByPage(page, size);

        List<ResponseQuestionDto> responseQuestionDtos =
                questionPage.stream().map(questionDtoConverter::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseQuestionDtos, pageable, total);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<ResponseQuestionDto> searchQuestions(String keyword, int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.searchQuestions(keyword, page, size);

        List<ResponseQuestionDto> responseQuestionDtos =
                questionPage.stream().map(questionDtoConverter::toResponseQuestionDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseQuestionDtos, pageable, total);
    }
}
