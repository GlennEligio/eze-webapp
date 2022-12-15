package com.eze.backend.spring.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class ProfessorDto {

    private Long id;

    @NotBlank(message = "Professor name can't be blank")
    private String name;

    @NotBlank(message = "Professor contact number can't be blank")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Professor contact number must be a valid PH mobile number")
    private String contactNumber;

    @NotBlank(message = "Email cant be blank")
    @Email(message = "Email must be a valid one", regexp=".+@.+\\..+")
    private String email;

    @URL(message = "Profile image url must be a valid url", regexp = "^(http|https)://.*")
    private String profile;
}
