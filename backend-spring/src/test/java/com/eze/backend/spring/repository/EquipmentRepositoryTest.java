package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Equipment equipment0;

    @BeforeEach
    void setup() {
        equipment0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        Equipment equipment1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
        entityManager.persist(equipment0);
        entityManager.persist(equipment1);
    }

    @Test
    @DisplayName("Find Equipment by Equipment Code using valid code")
    void findByEquipmentCode_usingValidEqCode_returnsEquipment() {
        String validEqCode = "EqCode0";

        Optional<Equipment> equipmentOptional = repository.findByEquipmentCode(validEqCode);

        assertTrue(equipmentOptional.isPresent());
        assertEquals(equipmentOptional.get(), equipment0);
    }

    @Test
    @DisplayName("Find Equipment by Eq Code using invalid code")
    void findByEquipmentCode_usingInvalidEqCode_returnsEmpty() {
        String invalidEqCode= "invalidEqCode";

        Optional<Equipment> equipmentOptional = repository.findByEquipmentCode(invalidEqCode);

        assertTrue(equipmentOptional.isEmpty());
    }

    @Test
    @DisplayName("Find Equipment by barcode using valid code")
    void findByBarcode_usingValidBarcode_returnsEquipment() {
        String validBarcode = "Barcode0";

        Optional<Equipment> equipmentOptional = repository.findByBarcode(validBarcode);

        assertTrue(equipmentOptional.isPresent());
        assertEquals(equipmentOptional.get(), equipment0);
    }

    @Test
    @DisplayName("Find Equipment by barcode using invalid code")
    void findByBarcode_usingInvalidBarcode_returnsEmpty() {
        String invalidBarcode = "invalidBarcode";

        Optional<Equipment> equipmentOptional = repository.findByBarcode(invalidBarcode);

        assertTrue(equipmentOptional.isEmpty());
    }

    @Test
    @DisplayName("Find All Non-deleted Equipments")
    void findAllNotDeleted_returnsNotDeletedEquipments() {
        List<Equipment> equipmentList = repository.findAllNotDeleted();

        assertEquals(0, equipmentList.stream().filter(Equipment::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Soft delete an Equipment")
    void softDelete_updatesEquipmentDeleteFlag() {
        String validEqCode = "EqCode0";

        repository.softDelete(validEqCode);
        Optional<Equipment> equipmentOptional = repository.findByEquipmentCode(validEqCode);

        assertTrue(equipmentOptional.isPresent());
        assertTrue(equipmentOptional.get().getDeleteFlag());
    }
}
