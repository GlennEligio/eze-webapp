package com.eze.backend.spring.enums;

import java.util.stream.Stream;

public enum TxStatus {
    PENDING("PENDING"), ACCEPTED("ACCEPTED"), DENIED("DENIED");

    private final String name;

    TxStatus(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TxStatus of(String name){
        return Stream.of(TxStatus.values())
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


}