package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {
    private Long id;
    private String studentNumber;
    private String fullName;
    private String yearAndSection;
    private String contactNumber;
    private String birthday;
    private String address;
    private String email;
    private String guardian;
    private String guardianNumber;
    private String yearLevel;
    private byte[] image;
}
