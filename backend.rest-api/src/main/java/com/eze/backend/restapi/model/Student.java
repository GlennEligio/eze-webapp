package com.eze.backend.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;
    @Column(unique = true, nullable = false, name = "student_number")
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
    @OneToMany(mappedBy = "borrower")
    // TODO: Temp fix for stackoverflow error, create DTO for this class that doesnt include this field
    @JsonIgnore
    private List<Transaction> transactions;
    @OneToOne(mappedBy = "student")
    private StudentFingerprint studentFingerprint;

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
