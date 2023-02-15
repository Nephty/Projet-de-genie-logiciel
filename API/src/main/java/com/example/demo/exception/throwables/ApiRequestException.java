package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public abstract class ApiRequestException extends RuntimeException {

    private final HttpStatus status;

    public ApiRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiRequestException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
