package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * Thrown whenever a client tries to access a resource that he doesn't have access
 */
public class AuthorizationException extends ApiRequestException{
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
