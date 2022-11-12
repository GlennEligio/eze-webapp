package com.eze.backend.spring.enums;

import java.util.stream.Stream;

public enum AccountType {
    SADMIN("SADMIN"), ADMIN("ADMIN"), SA("STUDENT_ASSISTANT"), STUDENT("STUDENT"), PROF("PROF");

    private final String name;

    AccountType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AccountType of(String name){
        return Stream.of(AccountType.values())
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
