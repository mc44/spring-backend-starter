package com.mfajardo.spring_backend_starter.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AppException {
    private static final String DEFAULT_MESSAGE = "Invalid or expired token";

    public InvalidTokenException() {
        super(DEFAULT_MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
