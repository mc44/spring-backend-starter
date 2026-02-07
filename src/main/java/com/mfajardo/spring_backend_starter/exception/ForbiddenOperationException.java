package com.mfajardo.spring_backend_starter.exception;


public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
