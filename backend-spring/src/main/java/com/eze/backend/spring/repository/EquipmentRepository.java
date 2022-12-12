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

    List<Equipment> findByIsBorrowed(boolean isBorrowed);

    @Query( "SELECT e FROM Equipment e WHERE e.deleteFlag=false")
    List<Equipment> findAllNotDeleted();

    @Query("SELECT e FROM Equipment e WHERE e.name LIKE concat('%', ?1 , '%') AND e.deleteFlag=false")
    List<Equipment> findByNameContaining(String name);

    //Soft delete.
    @Query("UPDATE Equipment e SET e.deleteFlag=true WHERE e.equipmentCode=?1")
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void softDelete(String equipmentCode);
}
