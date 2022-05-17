package com.eze.userservice.domain;

import java.util.stream.Stream;

public enum Role {
    SADMIN("ROLE_SADMIN"), ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String roleName;

    Role(String roleName){
        this.roleName = roleName;
    }

    private String getRoleName(){
        return this.roleName;
    }


    public static Role of(String roleName) {
        return Stream.of(Role.values())
                .filter(p -> p.getRoleName().equals(roleName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
