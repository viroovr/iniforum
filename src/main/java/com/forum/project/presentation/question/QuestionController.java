package com.forum.project.presentation.question;

import com.forum.project.application.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/q")
public class QuestionController {

    private QuestionService questionService;

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public ResponseEntity<String> postQuestion(
            @RequestBody RequestQuestionDto requestQuestionDto
    ) {
        questionService.createPost(requestQuestionDto);
        return ResponseEntity.ok(null);
    }

}
