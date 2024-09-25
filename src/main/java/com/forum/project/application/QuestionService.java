package com.forum.project.application;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.Question;
import com.forum.project.domain.QuestionRepository;
import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.question.RequestQuestionDto;
import com.forum.project.presentation.question.ResponseQuestionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Question createPost(RequestQuestionDto requestQuestionDto, String id) {
        Optional<User> user = userRepository.findById(Long.parseLong(id));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 유저입니다.");
        }
        User foundUser = user.get();
        String userId = foundUser.getUserId();

        Question question = new Question(requestQuestionDto.getTitle(), userId, requestQuestionDto.getContent(), requestQuestionDto.getTag());
        return questionRepository.save(question);
    }

    public ResponseQuestionDto findById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 질문입니다.");
        }
        return ResponseQuestionDto.toDto(question.get());
    }

    public Page<ResponseQuestionDto> getQuestionsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findAllByOrderByCreatedDateDesc(pageable);

        AtomicInteger startIndex = new AtomicInteger((int) (page * size + questionPage.getTotalElements()) + 1);
        Page<ResponseQuestionDto> responseQuestionDtoPage = questionPage
                                        .map(ResponseQuestionDto::toDto);
        responseQuestionDtoPage.forEach(question -> question.setPostNumber(startIndex.decrementAndGet()));
        return  responseQuestionDtoPage;

    }

    @Transactional
    public void deleteQuestion(Long id, String token) {
        String currentUserId = jwtTokenProvider.getUserId(token);
        Optional<Question> existingQuestion = questionRepository.findById(id);

        if(existingQuestion.isPresent()) {
            Question existing = existingQuestion.get();
            if(!existing.getUserId().equals(currentUserId)) {
                throw new BadCredentialsException("You are not authorized");
            }
            questionRepository.delete(existing);
        } else {

            throw new EntityNotFoundException("Question not found.");
        }


    }

    @Transactional
    public ResponseQuestionDto updateQuestion(Long id, RequestQuestionDto requestQuestionDto, String token) {
        String currentUserId = jwtTokenProvider.getUserId(token);
        Optional<Question> existingQuestion = questionRepository.findById(id);

        if (existingQuestion.isPresent()) {
            Question existing = existingQuestion.get();

            if(!existing.getUserId().equals(currentUserId)) {
                throw new BadCredentialsException("You are not authorized");

            }

            existing.setTitle(requestQuestionDto.getTitle());
            existing.setContent(requestQuestionDto.getContent());
            existing.setTag(requestQuestionDto.getTag());
            return ResponseQuestionDto.toDto(questionRepository.save(existing));
        } else {
            throw new EntityNotFoundException("Question not found.");

        }
    }

}
