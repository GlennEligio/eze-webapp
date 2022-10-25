package com.eze.backend.restapi.dtos;

public record StudentDto(Long id, String studentNumber, String fullName,
                             YearSectionDto yearAndSection, String contactNumber, String birthday,
                             String address, String email, String guardian,
                             String guardianNumber, Integer yearLevel) {
}
