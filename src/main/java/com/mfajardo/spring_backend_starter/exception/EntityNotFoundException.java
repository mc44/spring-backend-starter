package com.mfajardo.spring_backend_starter.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AppException {

    private static final String DEFAULT_MESSAGE = "Entity not found";

    public EntityNotFoundException() {
        super(DEFAULT_MESSAGE, HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
