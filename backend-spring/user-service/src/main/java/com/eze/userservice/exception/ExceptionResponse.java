package com.eze.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ExceptionResponse {
    private String message;
    private LocalDateTime timestamp;
    private String details;
}
