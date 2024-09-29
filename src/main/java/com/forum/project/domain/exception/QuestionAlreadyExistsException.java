package com.forum.project.domain.exception;

public class QuestionAlreadyExistsException extends RuntimeException{
    public QuestionAlreadyExistsException(String message) {
        super(message);
    }
}
