package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<RoomSchedule, Long> {

    Optional<RoomSchedule> findByScheduleCode(String code);
}
