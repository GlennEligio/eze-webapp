package com.eze.backend.restapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class StudentListDto{
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
    private Integer yearLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentListDto that = (StudentListDto) o;
        return Objects.equals(id, that.id) && Objects.equals(studentNumber, that.studentNumber) && Objects.equals(fullName, that.fullName) && Objects.equals(yearAndSection, that.yearAndSection) && Objects.equals(contactNumber, that.contactNumber) && Objects.equals(birthday, that.birthday) && Objects.equals(address, that.address) && Objects.equals(email, that.email) && Objects.equals(guardian, that.guardian) && Objects.equals(guardianNumber, that.guardianNumber) && Objects.equals(yearLevel, that.yearLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentNumber, fullName, yearAndSection, contactNumber, birthday, address, email, guardian, guardianNumber, yearLevel);
    }
}
