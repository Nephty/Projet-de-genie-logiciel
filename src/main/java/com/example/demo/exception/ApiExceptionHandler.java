package com.example.demo.exception;

import com.example.demo.exception.throwables.ApiRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ApiExceptionHandler {
    /**
     * This method is called whenever an ApiRequestException is raised
     * It writes the error message in the body and sets the response status has the exception dictates
     * @param e error that was raised
     */
    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<String> handleApiRequestException(ApiRequestException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}
