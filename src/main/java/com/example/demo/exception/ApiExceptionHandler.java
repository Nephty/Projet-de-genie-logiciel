package com.example.demo.exception;

import com.example.demo.exception.throwables.ApiRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<String> handleApiRequestException(ApiRequestException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
}
