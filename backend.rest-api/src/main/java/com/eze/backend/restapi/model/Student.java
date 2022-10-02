package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
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

    public void update(@NonNull Student newStudent) {
        if(newStudent.getAddress() != null) {
            this.setAddress(newStudent.getAddress());
        }
        if(newStudent.getFullName() != null) {
            this.setFullName(newStudent.getFullName());
        }
        if(newStudent.getYearAndSection() != null) {
            this.setYearAndSection(newStudent.getYearAndSection());
        }
        if(newStudent.getContactNumber() != null) {
            this.setContactNumber(newStudent.getContactNumber());
        }
        if(newStudent.getBirthday() != null) {
            this.setBirthday(newStudent.getBirthday());
        }
        if(newStudent.getAddress() != null) {
            this.setAddress(newStudent.getAddress());
        }
        if(newStudent.getEmail() != null) {
            this.setEmail(newStudent.getEmail());
        }
        if(newStudent.getGuardian() != null) {
            this.setGuardian(newStudent.getGuardian());
        }
        if(newStudent.getGuardianNumber() != null) {
            this.setGuardianNumber(newStudent.getGuardianNumber());
        }
        if(newStudent.getYearLevel() != null) {
            this.setYearLevel(newStudent.getYearLevel());
        }
    }
}
