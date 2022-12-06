package com.eze.backend.spring.util;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class ObjectIdGenerator {
    public String createHexId() {
        return new ObjectId().toHexString();
    }
    public String createId() {
        return new ObjectId().toString();
    }
}
