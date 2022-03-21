package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * Thrown whenever there is a conflict with what the user want to do with de DB and the state of the DB
 * Ex: a wrong FK has been specified
 *     the transaction amount is greater than the account amount
 */
public class ConflictException extends ApiRequestException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
