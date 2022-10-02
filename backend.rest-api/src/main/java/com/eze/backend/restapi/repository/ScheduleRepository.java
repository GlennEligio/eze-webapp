package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<RoomSchedule, Long> {

    Optional<RoomSchedule> findByScheduleCode(String code);
}
