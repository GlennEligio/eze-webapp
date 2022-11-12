//package com.eze.backend.restapi.repository;
//
//import com.eze.backend.restapi.model.StudentFingerprint;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.Optional;
//
//public interface StudentFingerprintRepository extends JpaRepository<StudentFingerprint, Long> {
//
//    @Query("SELECT fp FROM StudentFingerprint fp " +
//            "LEFT JOIN fp.student st " +
//            "WHERE st.studentNumber=?1")
//    Optional<StudentFingerprint> findByStudentNumber(String studentNumber);
//}
