package com.forum.project.application;

import com.forum.project.domain.Question;
import com.forum.project.domain.QuestionRepository;
import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.question.RequestQuestionDto;
import com.forum.project.presentation.question.ResponseQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

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

}
