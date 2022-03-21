package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * Thrown whenever there is an error in the authentication process
 */
public class AuthenticationException extends  ApiRequestException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.UNAUTHORIZED);
    }
}
