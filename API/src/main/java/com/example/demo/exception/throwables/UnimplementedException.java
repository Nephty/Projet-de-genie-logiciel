package com.example.demo.exception.throwables;

import org.springframework.http.HttpStatus;

/**
 * The end point is not yet implemented only for dev purposes
 */
public class UnimplementedException extends ApiRequestException{
    public UnimplementedException() {
        super("Endpoint not yet implemented ask nicely to Augustin", HttpStatus.I_AM_A_TEAPOT);
    }

    public UnimplementedException(Throwable cause) {
        super("Endpoint not yet implemented ask nicely to Augustin", cause, HttpStatus.I_AM_A_TEAPOT);
    }
}
