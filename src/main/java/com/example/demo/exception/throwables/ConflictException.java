package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiRequestException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
