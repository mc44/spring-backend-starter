package com.mfajardo.spring_backend_starter.exception;


import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends AppException {
    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
