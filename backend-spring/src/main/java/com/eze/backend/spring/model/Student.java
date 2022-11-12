package com.eze.backend.spring.model;

import com.eze.backend.spring.dtos.StudentDto;
import com.eze.backend.spring.dtos.StudentListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    @Pattern(regexp = "^\\d{4}-\\d{5}-[(a-z)|(A-Z)]{2}-\\d{2}$", message = "Invalid PUP Student Number")
    @NotBlank(message = "Student number can't be blank")
    private String studentNumber;

    @NotBlank(message = "Full name cant be blank")
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "yearAndSection", referencedColumnName = "sectionName")
    @NotNull(message = "YearSection must be present")
    @Valid
    private YearSection yearAndSection;

    @NotBlank(message = "Contact number can't be blank")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Contact number must be a valid PH mobile number")
    private String contactNumber;
    private String birthday;
    private String address;
    private String email;
    private String guardian;
    private String guardianNumber;

    @ManyToOne
    @JoinColumn(name = "yearLevel", referencedColumnName = "yearName")
    @NotNull(message="Year level must be present")
    @Valid
    private YearLevel yearLevel;
    private byte[] image;
    private Boolean deleteFlag;

    @OneToMany(mappedBy = "borrower")
    private List<Transaction> transactions;
//    @OneToOne(mappedBy = "student")
//    private StudentFingerprint studentFingerprint;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(studentNumber, student.studentNumber) && Objects.equals(fullName, student.fullName) && Objects.equals(yearAndSection, student.yearAndSection) && Objects.equals(contactNumber, student.contactNumber) && Objects.equals(birthday, student.birthday) && Objects.equals(address, student.address) && Objects.equals(email, student.email) && Objects.equals(guardian, student.guardian) && Objects.equals(guardianNumber, student.guardianNumber) && Objects.equals(yearLevel, student.yearLevel) && Arrays.equals(image, student.image) && Objects.equals(deleteFlag, student.deleteFlag);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, studentNumber, fullName, yearAndSection, contactNumber, birthday, address, email, guardian, guardianNumber, yearLevel, deleteFlag);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

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
        if(newStudent.getDeleteFlag() != null) {
            this.setDeleteFlag(newStudent.getDeleteFlag());
        }
    }

    public static StudentListDto toStudentListDto(Student student) {
        return new StudentListDto(student.getId(),
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

    public static StudentDto toStudentDto(Student student) {
        return new StudentDto(student.getId(),
                student.getStudentNumber(),
                student.getFullName(),
                YearSection.toYearSectionDto(student.getYearAndSection()),
                student.getContactNumber(),
                student.getBirthday(),
                student.getAddress(),
                student.getEmail(),
                student.getGuardian(),
                student.getGuardianNumber(),
                YearLevel.toYearLevelDto(student.getYearLevel()));
    }

    public static Student toStudent(StudentDto dto) {
        Student student = new Student();
        student.setStudentNumber(dto.getStudentNumber());
        student.setFullName(dto.getFullName());
        student.setYearAndSection(YearSection.toYearSection(dto.getYearAndSection()));
        student.setContactNumber(dto.getContactNumber());
        student.setBirthday(dto.getBirthday());
        student.setAddress(dto.getAddress());
        student.setEmail(dto.getEmail());
        student.setGuardian(dto.getGuardian());
        student.setGuardianNumber(dto.getGuardianNumber());
        student.setYearLevel(YearLevel.toYearLevel(dto.getYearLevel()));
        return student;
    }
}
