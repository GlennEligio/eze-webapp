package com.eze.userservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class UserExceptionResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllException (Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException (ApiException ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false));
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(ex.getBindingResult().getAllErrors().toString()
                , LocalDateTime.now(), request.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
