package com.eze.backend.restapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Long id;
    private String subjectCode;
    private String subjectName;
    private String day;
    private String time;
    private String room;
    private Professor professor;
    private String yearLevel;
    private String yearAndSection;
}
