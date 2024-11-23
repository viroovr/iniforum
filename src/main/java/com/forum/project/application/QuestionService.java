package com.forum.project.application;

import com.forum.project.application.security.jwt.JwtTokenProvider;
import com.forum.project.domain.entity.Question;
import com.forum.project.domain.exception.UserNotFoundException;
import com.forum.project.domain.repository.QuestionRepository;
import com.forum.project.domain.entity.User;
import com.forum.project.domain.repository.UserRepository;
import com.forum.project.presentation.dtos.question.RequestQuestionDto;
import com.forum.project.presentation.dtos.question.ResponseQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Question createPost(RequestQuestionDto requestQuestionDto, String jwt) {
        String userId = jwtTokenProvider.getUserId(jwt);
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("유저를 찾을수없습니다."));

        Question question = new Question(
                requestQuestionDto.getTitle(),
                user.getUserId(),
                requestQuestionDto.getContent(),
                requestQuestionDto.getTag()
        );
        return questionRepository.save(question);
    }

    public ResponseQuestionDto findById(Long id) {
        Question question = questionRepository.findById(id);
        return ResponseQuestionDto.toDto(question);
    }

    public Page<ResponseQuestionDto> getQuestionsByPage(int page, int size) {

        List<Question> questionPage = questionRepository.getQuestionByPage(page, size);
        long total = questionRepository.count();

        List<ResponseQuestionDto> responseQuestionDtos =
                questionPage.stream().map(ResponseQuestionDto::toDto).toList();

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(responseQuestionDtos, pageable, total);
    }

    @Transactional
    public void deleteQuestion(Long id, String token) {
        validateQuestionByUserId(id, token);
        questionRepository.deleteById(id);
    }

    @Transactional
    public ResponseQuestionDto updateQuestion(Long id, RequestQuestionDto requestQuestionDto, String token) {
        Question question = validateQuestionByUserId(id, token);

        question.setTitle(requestQuestionDto.getTitle());
        question.setContent(requestQuestionDto.getContent());
        question.setTag(requestQuestionDto.getTag());

        return ResponseQuestionDto.toDto(questionRepository.save(question));
    }

    private Question validateQuestionByUserId(Long id, String token) {
        String currentUserId = jwtTokenProvider.getUserId(token);
        Question question = questionRepository.findById(id);

        if(!question.getUserId().equals(currentUserId)) {
            throw new BadCredentialsException("접근할 수 없는 권한입니다.");
        }
        return question;
    }

    public Page<ResponseQuestionDto> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questionRepository.searchQuestions(keyword, pageable)
                .map(ResponseQuestionDto::toDto);
    }

}
