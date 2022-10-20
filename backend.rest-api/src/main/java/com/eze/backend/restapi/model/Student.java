package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.StudentDto;
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
@Entity
public class Student implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;
    @Column(unique = true, nullable = false)
    private String studentNumber;
    private String fullName;
    @ManyToOne
    @JoinColumn(name = "yearAndSection", referencedColumnName = "sectionName")
    private YearSection yearAndSection;
    private String contactNumber;
    private String birthday;
    private String address;
    private String email;
    private String guardian;
    private String guardianNumber;
    @ManyToOne
    @JoinColumn(name = "yearLevel", referencedColumnName = "yearName")
    private YearLevel yearLevel;
    private byte[] image;
    @OneToMany(mappedBy = "borrower")
    // TODO: Temp fix for stackoverflow error, create DTO for this class that doesnt include this field
    @JsonIgnore
    private List<Transaction> transactions;
//    @OneToOne(mappedBy = "student")
//    private StudentFingerprint studentFingerprint;

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

    public static StudentDto toStudentDto(Student student) {
        return new StudentDto(student.getId(),
                student.getStudentNumber(),
                student.getFullName(),
                student.getYearAndSection().getSectionName(),
                student.getContactNumber(),
                student.getBirthday(),
                student.getAddress(),
                student.getEmail(),
                student.getGuardian(),
                student.getGuardianNumber(),
                student.getYearLevel().getYearNumber());
    }
}
