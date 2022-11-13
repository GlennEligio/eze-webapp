package com.eze.backend.spring.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ITimeStampProviderImpl implements ITimeStampProvider{
    @Override
    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
