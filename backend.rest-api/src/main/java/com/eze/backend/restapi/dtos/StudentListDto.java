package com.eze.backend.restapi.dtos;

public record StudentListDto(Long id, String studentNumber, String fullName,
                         String yearAndSection, String contactNumber, String birthday,
                         String address, String email, String guardian,
                         String guardianNumber, Integer yearLevel) {
}
