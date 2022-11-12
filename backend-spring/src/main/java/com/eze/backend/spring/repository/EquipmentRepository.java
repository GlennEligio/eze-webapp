package com.eze.backend.spring.repository;

import com.eze.backend.spring.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByEquipmentCode(String equipmentCode);
    Optional<Equipment> findByBarcode(String barcode);
    Optional<Equipment> findByName(String name);

    @Query( "SELECT e FROM Equipment e WHERE e.deleteFlag=false")
    List<Equipment> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Equipment e SET e.deleteFlag=true WHERE e.equipmentCode=?1")
    @Modifying
    void softDelete(String equipmentCode);
}
