package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}
