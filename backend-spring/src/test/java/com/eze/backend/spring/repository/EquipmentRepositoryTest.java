package com.eze.backend.spring.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.model.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class EquipmentRepositoryTest {

    @Autowired
    private EquipmentRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Equipment equipment0;
    private List<Equipment> equipmentList;

    @BeforeEach
    void setup() {
        equipment0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), false, false, false);
        Equipment equipment1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
        Equipment equipment2 = new Equipment("EqCode02", "Name2", "Barcode2", EqStatus.GOOD, LocalDateTime.now(), false, true, true);
        equipmentList = new ArrayList<>();
        equipmentList.add(equipment0);
        equipmentList.add(equipment1);
        equipmentList.add(equipment2);
        entityManager.persist(equipment0);
        entityManager.persist(equipment1);
        entityManager.persist(equipment2);
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

    @Test
    @DisplayName("Find all non-borrowed Equipments")
    void findAllNonBorrowed_returnsNonBorrowedEquipments() {
        List<Equipment> nonBorrowedEquipments = repository.findByIsBorrowed(false);

        assertEquals(0, nonBorrowedEquipments.stream().filter(Equipment::getIsBorrowed).count());
    }

    @Test
    @DisplayName("Find equipments whose name contains the string input")
    void findByNameContaining_returnsEquipmentsWhoseNameContainsTheString() {
        String nameQuery = "1";
        List<Equipment> expectedList = equipmentList.stream().filter(e -> e.getName().contains(nameQuery)).toList();
        List<Equipment> resultList = repository.findByNameContaining(nameQuery);

        assertEquals(expectedList, resultList);
    }
}
