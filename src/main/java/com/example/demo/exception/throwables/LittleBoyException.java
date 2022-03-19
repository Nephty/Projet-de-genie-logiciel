package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public class LittleBoyException extends ApiRequestException {
    public LittleBoyException() {
        super("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public LittleBoyException(Throwable cause) {
        super("Server error", cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
