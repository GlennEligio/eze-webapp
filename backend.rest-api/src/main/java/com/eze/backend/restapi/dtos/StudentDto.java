package com.eze.backend.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class StudentDto {
    private Long id;
    @Pattern(regexp = "^\\d{4}-\\d{5}-[(a-z)|(A-Z)]{2}-\\d{2}$", message = "Invalid PUP Student Number")
    @NotBlank(message = "Student number can't be blank")
    private String studentNumber;
    @NotBlank(message = "Full name cant be blank")
    private String fullName;
    @NotNull(message = "YearSection must be present")
    @Valid
    private YearSectionDto yearAndSection;
    @NotBlank(message = "Contact number can't be blank")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Contact number must be a valid PH mobile number")
    private String contactNumber;
    private String birthday;
    private String address;
    private String email;
    private String guardian;
    private String guardianNumber;
    @NotNull(message="Year level must be present")
    @Valid
    private YearLevelDto yearLevel;
    @URL(message = "Profile image url must be a valid url", regexp = "^(http|https)://.*")
    private String profile;
}
