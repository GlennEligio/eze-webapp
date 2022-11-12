package com.eze.backend.spring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoomSchedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO: Create Custom Generator for the Schedule entity schedule code with Day, Time, and Room
    @Column(nullable = false, unique = true)
    private String scheduleCode;
    private String subjectCode;
    private String subjectName;
    // TODO: Add NonNull constraint to day, time, and room
    private String day;
    private String time;
    private String room;
    @JoinColumn(name = "professor_name", referencedColumnName = "name")
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Professor professor;
    private String yearLevel;
    private String yearAndSection;

    public void update(RoomSchedule roomScheduleNew) {
        if(roomScheduleNew.getSubjectCode() != null) {
            this.subjectCode = roomScheduleNew.subjectCode;
        }
        if(roomScheduleNew.getSubjectName() != null) {
            this.subjectName = roomScheduleNew.getSubjectName();
        }
        if(roomScheduleNew.getDay() != null) {
            this.day = roomScheduleNew.getDay();
        }
        if(roomScheduleNew.getTime() != null) {
            this.time = roomScheduleNew.getTime();
        }
        if(roomScheduleNew.getRoom() != null) {
            this.room = roomScheduleNew.getRoom();
        }
        if(roomScheduleNew.getProfessor() != null) {
            this.professor = roomScheduleNew.professor;
        }
        if(roomScheduleNew.getYearAndSection() != null) {
            this.yearAndSection = roomScheduleNew.getYearAndSection();
        }
        if(roomScheduleNew.getYearLevel() != null) {
            this.yearLevel = roomScheduleNew.getYearLevel();
        }
    }
}
