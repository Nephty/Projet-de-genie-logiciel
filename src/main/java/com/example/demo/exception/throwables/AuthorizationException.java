package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends ApiRequestException{
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
