package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * When the user want to access data that is not on the DB
 */
public class ResourceNotFound extends ApiRequestException {
    public ResourceNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFound(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }
}
