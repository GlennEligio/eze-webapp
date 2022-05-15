package com.eze.userservice.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "user_username")
    private String username;

    @NotBlank
    @Column(name ="user_password")
    private String password;

    private Boolean deleteFlag;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String name, String username, String password, Boolean deleteFlag, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.deleteFlag = deleteFlag;
        this.role = role;
    }
}
