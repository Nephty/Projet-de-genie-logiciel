package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * When a user or a bank tries to create an account that already exists
 */
public class UserAlreadyExist extends ApiRequestException {
    public enum Reason {
        USERNAME("USERNAME"),
        EMAIL("EMAIL"),
        SWIFT("SWIFT"),
        NAME("NAME"),
        ID("ID");

        private final String name;

        Reason(String name) {
            this.name = name;
        }
    }
    public UserAlreadyExist(Reason reason) {
        super(reason.name, HttpStatus.FORBIDDEN);
    }

}
