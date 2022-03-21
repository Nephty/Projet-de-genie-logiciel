package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * The server is nuked
 * Used for cases that are not supposed to happen during runtime
 */
public class LittleBoyException extends ApiRequestException {
    public LittleBoyException() {
        super("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public LittleBoyException(Throwable cause) {
        super("Server error", cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
