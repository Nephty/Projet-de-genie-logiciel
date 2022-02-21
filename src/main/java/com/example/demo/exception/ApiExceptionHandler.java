package com.example.demo.exception;

import com.example.demo.exception.throwables.ApiRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                e,
                e.getStatus(),
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity<>(apiException, e.getStatus());
    }
}
