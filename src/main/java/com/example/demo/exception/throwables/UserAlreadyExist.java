package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

public class UserAlreadyExist extends ApiRequestException {
    public enum Reason {
        USERNAME("USERNAME"),
        EMAIL("EMAIL"),
        ID("ID");

        private final String name;

        Reason(String name) {
            this.name = name;
        }
    }
    public UserAlreadyExist(Reason reason) {
        super(reason.name, HttpStatus.FORBIDDEN);
    }

    public UserAlreadyExist(Reason reason, Throwable cause) {
        super(reason.name, cause, HttpStatus.UNAUTHORIZED);
    }

}
