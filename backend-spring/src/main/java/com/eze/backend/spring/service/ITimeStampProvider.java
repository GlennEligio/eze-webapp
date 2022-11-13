package com.eze.backend.spring.service;

import java.time.LocalDateTime;

public interface ITimeStampProvider {
    LocalDateTime getNow();
}
