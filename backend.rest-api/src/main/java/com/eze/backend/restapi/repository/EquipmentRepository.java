package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByEquipmentCode(String equipmentCode);
    Optional<Equipment> findByBarcode(String barcode);
}
