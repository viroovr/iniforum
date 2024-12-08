package com.forum.project.application.question;

import com.forum.project.application.security.jwt.TokenService;
import com.forum.project.domain.entity.Question;
import com.forum.project.domain.exception.ApplicationException;
import com.forum.project.domain.exception.ErrorCode;
import com.forum.project.domain.repository.QuestionRepository;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.repository.TotalCountRepository;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

    private final TokenService tokenService;

    @Transactional
    public ResponseQuestionDto createPost(RequestQuestionDto requestQuestionDto, String jwt) {
        long id = tokenService.getId(jwt);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Question question = Question.builder()
                .title(requestQuestionDto.getTitle())
                .userId(user.getUserId())
                .content(requestQuestionDto.getContent())
                .tag(requestQuestionDto.getTag())
                .build();

        try {
            Question savedQuestion = questionRepository.save(question);
            totalCountRepository.incrementTotalCount();
            return ResponseQuestionDto.toDto(savedQuestion);
        } catch (EntityNotFoundException e) {
            throw new ApplicationException(ErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public ResponseQuestionDto findById(Long id) {
        Question question = questionRepository
                .findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.QUESTION_NOT_FOUND));
        return ResponseQuestionDto.toDto(question);
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

    private Long getTotalQuestionCount() {
        Long total = totalCountRepository.getTotalCount();
        if (total == null) {
            total = questionRepository.count();
            totalCountRepository.setTotalCount(total);
        }
        return total;
    }

    @Transactional
    public ResponseQuestionDto updateQuestion(Long questionId, RequestQuestionDto requestQuestionDto, String accessToken) {
        Question question = validateQuestion(questionId, accessToken);

        question.setTitle(requestQuestionDto.getTitle());
        question.setContent(requestQuestionDto.getContent());
        question.setTag(requestQuestionDto.getTag());

        return ResponseQuestionDto.toDto(questionRepository.save(question));
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<ResponseQuestionDto> getQuestionsByPage(int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.getQuestionByPage(page, size);

        List<ResponseQuestionDto> responseQuestionDtos =
                questionPage.stream().map(ResponseQuestionDto::toDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseQuestionDtos, pageable, total);
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Page<ResponseQuestionDto> searchPosts(String keyword, int page, int size) {
        Long total = getTotalQuestionCount();

        List<Question> questionPage = questionRepository.searchQuestions(keyword, page, size);

        List<ResponseQuestionDto> responseQuestionDtos =
                questionPage.stream().map(ResponseQuestionDto::toDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseQuestionDtos, pageable, total);
    }
}
