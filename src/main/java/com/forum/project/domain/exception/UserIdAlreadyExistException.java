package com.forum.project.domain.exception;

public class UserIdAlreadyExistException extends RuntimeException{
    public UserIdAlreadyExistException(String e) {
        super(e);
    }
}
