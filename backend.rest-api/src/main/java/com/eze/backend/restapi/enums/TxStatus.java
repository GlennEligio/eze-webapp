package com.eze.backend.restapi.enums;

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

    public static EqStatus of(String name){
        return Stream.of(EqStatus.values())
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }


}