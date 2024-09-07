package com.forum.project.application;

import com.forum.project.domain.Question;
import com.forum.project.domain.QuestionRepository;
import com.forum.project.domain.User;
import com.forum.project.domain.UserRepository;
import com.forum.project.presentation.question.RequestQuestionDto;
import com.forum.project.presentation.question.ResponseQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Question findById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 질문입니다.");
        }
        return question.get();
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

}
