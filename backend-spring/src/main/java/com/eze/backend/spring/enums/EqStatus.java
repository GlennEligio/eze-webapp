package com.eze.backend.spring.enums;

import java.util.stream.Stream;

public enum EqStatus {
    DEFECTIVE("DEFECTIVE"), GOOD("GOOD");

    private final String name;

    EqStatus(String name){
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