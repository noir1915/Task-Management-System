package com.example.Task_Management_System.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> exceptionHandler(ResourceNotFoundException e) {
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_MODIFIED.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
