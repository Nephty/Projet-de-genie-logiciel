package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public class MissingParamException extends ApiRequestException {
    public MissingParamException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public MissingParamException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }
}
