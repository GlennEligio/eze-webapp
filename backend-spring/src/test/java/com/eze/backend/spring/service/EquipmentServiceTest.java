package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.repository.EquipmentRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class EquipmentServiceTest {

    @Mock
    private EquipmentRepository repository;

    @Mock
    private ObjectIdGenerator idGenerator;

    @InjectMocks
    private EquipmentService service;

    private List<Equipment> equipmentList;
    private Equipment equipment0;

    @BeforeEach
    void setup () {
        equipment0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        Equipment equipment1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
        equipmentList = List.of(equipment0, equipment1);
    }

    @Test
    @DisplayName("Find all Equipments")
    void getAll_returnsEquipments() {
        Mockito.when(repository.findAll()).thenReturn(equipmentList);

        List<Equipment> equipments = service.getAll();

        assertNotNull(equipments);
        assertEquals(equipmentList, equipments);
    }

    @Test
    @DisplayName("Find all not deleted Equipments")
    void getAllNotDeleted_returnsNotDeletedEquipments () {
        List<Equipment> notDeletedEquipments = equipmentList.stream().filter(e -> !e.getDeleteFlag()).toList();
        Mockito.when(repository.findAllNotDeleted()).thenReturn(notDeletedEquipments);

        List<Equipment> equipments = service.getAllNotDeleted();

        assertNotNull(equipments);
        assertEquals(0, equipments.stream().filter(Equipment::getDeleteFlag).count());
    }

    @Test
    @DisplayName("Find Equipment with valid code")
    void get_usingValidCode_returnsEquipment() {
        String validCode= equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(validCode)).thenReturn(Optional.of(equipment0));

        Equipment equipment = service.get(validCode);
        assertNotNull(equipment);
        assertEquals(equipment0, equipment);
    }

    @Test
    @DisplayName("Find Equipment with invalid code")
    void get_usingInvalidCode_throwsException() {
        String invalidCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(invalidCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.get(invalidCode));
    }

    @Test
    @DisplayName("Create Equipment with available equipment code and barcode")
    void create_usingAvailableCode_returnsNewEquipment() {
        String encryptedCode = new ObjectId().toHexString();
        Equipment updatedEq = new Equipment(encryptedCode, equipment0.getName(), equipment0.getBarcode(), equipment0.getStatus(), equipment0.getDefectiveSince(), equipment0.getIsDuplicable(), equipment0.getIsBorrowed(), equipment0.getDeleteFlag());
        Mockito.when(repository.findByEquipmentCode(equipment0.getEquipmentCode())).thenReturn(Optional.empty());
        Mockito.when(repository.findByBarcode(equipment0.getBarcode())).thenReturn(Optional.empty());
        Mockito.when(idGenerator.createId()).thenReturn(encryptedCode);
        Mockito.when(repository.save(updatedEq)).thenReturn(updatedEq);

        Equipment equipment = service.create(equipment0);

        assertNotNull(equipment);
        assertEquals(updatedEq, equipment);
    }

    @Test
    @DisplayName("Create Equipment with unavailable barcode")
    void create_usingAlreadyTakenBarcode_throwsException() {
        String takenBarcode = equipment0.getBarcode();
        Mockito.when(repository.findByBarcode(takenBarcode)).thenReturn(Optional.of(equipment0));

        assertThrows(ApiException.class, () -> service.create(equipment0));
    }

    @Test
    @DisplayName("Create Equipment with invalid Equipment Code")
    void create_usingAlreadyTakenEqCode_throwsException
}
