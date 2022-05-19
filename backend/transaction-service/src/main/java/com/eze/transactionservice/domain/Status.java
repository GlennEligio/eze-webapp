package com.eze.transactionservice.domain;

import java.util.stream.Stream;

public enum Status {
    ACCEPTED("ACCEPTED"), PENDING("PENDING"), DENIED("DENIED");

    private final String statusName;

    Status(String statusName){
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public static Status of(String statusName){
        return Stream.of(Status.values())
                .filter(s -> s.getStatusName().equals(statusName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
