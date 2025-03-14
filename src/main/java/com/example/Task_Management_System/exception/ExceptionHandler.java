package com.example.Task_Management_System.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers, HttpStatusCode status, @NonNull WebRequest request) {

        Map<String, String> responseBody = ex.getBindingResult().getAllErrors().stream()
                .map(error -> (FieldError) error)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return createResponseEntity(headers, responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                logger.warn("Response already committed. Ignoring: " + ex);
                return null;
            }
        }

        Map<String, String> responseBody = Map.of(
                "message", ex instanceof HttpMessageNotReadableException ? ex.getLocalizedMessage() : "An error occurred"
        );

        return createResponseEntity(headers, responseBody);
    }

    private ResponseEntity<Object> createResponseEntity(HttpHeaders headers, Map<String, String> body) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(body);
    }
}
