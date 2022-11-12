//package com.eze.backend.restapi.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class StudentFingerprint {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private Byte[] fingerprint;
//    @OneToOne
//    @JoinColumn(name = "student_number", referencedColumnName = "studentNumber")
//    private Student student;
//
//    public void update(StudentFingerprint sfNew) {
//        if(sfNew.getFingerprint() != null) {
//            this.fingerprint = sfNew.getFingerprint();
//        }
//        if(sfNew.getStudent() != null) {
//            this.student = sfNew.getStudent();
//        }
//    }
//}
