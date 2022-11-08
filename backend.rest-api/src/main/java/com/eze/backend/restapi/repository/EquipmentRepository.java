package com.eze.backend.restapi.repository;

import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByEquipmentCode(String equipmentCode);
    Optional<Equipment> findByBarcode(String barcode);

    @Query( "SELECT e FROM Equipment e WHERE e.deleteFlag=false")
    List<Equipment> findAllNotDeleted();

    //Soft delete.
    @Query("UPDATE Equipment e SET e.deleteFlag=true WHERE e.equipmentCode=?1")
    @Modifying
    void softDelete(String equipmentCode);
}
