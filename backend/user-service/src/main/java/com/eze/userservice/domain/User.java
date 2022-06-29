package com.eze.userservice.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Username must not be blank")
    @Column(name = "user_username", unique = true)
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Column(name ="user_password")
    private String password;

    private Boolean deleteFlag;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    @Column(name = "avatar", columnDefinition = "MEDIUMBLOB")
    private byte[] avatar;

    public User(String name, String username, String password, Boolean deleteFlag, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.deleteFlag = deleteFlag;
        this.role = role;
    }

    public User(Long id, String name, String username, String password, Boolean deleteFlag, Role role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.deleteFlag = deleteFlag;
        this.role = role;
    }
}
