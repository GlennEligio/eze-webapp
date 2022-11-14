package com.eze.backend.spring.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeStampProvider {

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
