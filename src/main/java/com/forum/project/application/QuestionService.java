package com.forum.project.application;

import com.forum.project.domain.Question;
import com.forum.project.domain.QuestionRepository;
import com.forum.project.presentation.question.RequestQuestionDto;
import com.forum.project.presentation.question.ResponseQuestionDto;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private QuestionRepository questionRepository;

    public Question createPost(RequestQuestionDto requestQuestionDto) {

        return questionRepository.save(RequestQuestionDto.toQuestion(requestQuestionDto));
    }

}
