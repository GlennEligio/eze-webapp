package com.eze.backend.restapi.exception;

import com.eze.backend.restapi.dtos.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class EzeExceptionResponseHandler extends ResponseEntityExceptionHandler {

    // General Exception handler
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllException (Exception ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles SQL Exceptions, mostly Unique Constraint
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleSqlException (DataIntegrityViolationException ex, WebRequest request){
        String message = ex.getMessage();

        if(ex.getCause().getCause() instanceof SQLException e){
            message = e.getMessage();
        }

        ExceptionResponse response = new ExceptionResponse(message,
                LocalDateTime.now(),
                request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle ApiExceptions
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ExceptionResponse> handleApiException (ApiException ex, WebRequest request){
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false));
        return new ResponseEntity<>(response, ex.getStatus());
    }


    // Handles ConstraintViolationException cases
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException (ConstraintViolationException ex, WebRequest request){
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation cv : ex.getConstraintViolations()) {
            errors.add(cv.getPropertyPath() + ": " + cv.getConstraintDescriptor().getMessageTemplate());
        }
        ExceptionResponse response = new ExceptionResponse(errors.toString(),
                LocalDateTime.now(),
                request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle exceptions for Validation Constraints in DTOs
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        ExceptionResponse response = new ExceptionResponse(errors.toString(),
                LocalDateTime.now(),
                request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
