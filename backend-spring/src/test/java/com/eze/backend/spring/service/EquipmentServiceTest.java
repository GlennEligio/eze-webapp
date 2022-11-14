package com.eze.backend.spring.service;

import static org.junit.jupiter.api.Assertions.*;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.repository.EquipmentRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Equipment equipment0, equipment1;

    @BeforeEach
    void setup () {
        equipment0 = new Equipment("EqCode0", "Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        equipment1 = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
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
    void create_usingAlreadyTakenEqCode_throwsException() {
        String takenEqCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(takenEqCode)).thenReturn(Optional.of(equipment0));

        assertThrows(ApiException.class, () -> service.create(equipment0));
    }

    @Test
    @DisplayName("Update non-existent Equipment")
    void update_aNonExistentEquipment_throwsException() {
        String invalidEqCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(invalidEqCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.update(equipment0, invalidEqCode));
    }

    @Test
    @DisplayName("Update existing Equipment")
    void update_existentEquipment_returnsUpdatedEquipment() {
        String validEqCode = equipment0.getEquipmentCode();
        Equipment newEq = new Equipment("EqCode01", "Name1", "Barcode1", EqStatus.GOOD, LocalDateTime.now(), true, false, true);
        newEq.setEquipmentCode(validEqCode);
        Mockito.when(repository.findByEquipmentCode(validEqCode)).thenReturn(Optional.of(equipment0));
        Mockito.when(repository.save(newEq)).thenReturn(newEq);

        Equipment equipment = service.update(newEq, validEqCode);

        assertNotNull(equipment);
        assertEquals(newEq, equipment);
    }

    @Test
    @DisplayName("Delete non-existent Equipment")
    void delete_nonExistentEquipment_throwsException() {
        String invalidEqCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(invalidEqCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.delete(invalidEqCode));
    }

    @Test
    @DisplayName("Delete existing Equipment")
    void delete_existentEquipment_doesNotThrowException() {
        String validEqCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(validEqCode)).thenReturn(Optional.of(equipment0));

        assertDoesNotThrow(() -> service.delete(validEqCode));
    }

    @Test
    @DisplayName("Soft deletes non-existent Equipment")
    void softDelete_nonExistentEquipment_throwsException() {
        String invalidEqCode = equipment0.getEquipmentCode();
        Mockito.when(repository.findByEquipmentCode(invalidEqCode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.softDelete(invalidEqCode));
    }

    @Test
    @DisplayName("Soft deletes existing Equipment with deleteFlag true")
    void softDelete_whereEquipmentAlreadySoftDeleted_throwsException() {
        String validEqCode = equipment0.getEquipmentCode();
        equipment0.setDeleteFlag(true);
        Mockito.when(repository.findByEquipmentCode(validEqCode)).thenReturn(Optional.of(equipment0));

        assertThrows(ApiException.class, () -> service.softDelete(validEqCode));
    }

    @Test
    @DisplayName("Soft deletes existing Equipment with deleteFlag false")
    void softDelete_whereEquipmentNotYetSoftDeleted_doesNotThrowExcetion() {
        String validEqCode = equipment0.getEquipmentCode();
        equipment0.setDeleteFlag(false);
        Mockito.when(repository.findByEquipmentCode(validEqCode)).thenReturn(Optional.of(equipment0));

        assertDoesNotThrow(() -> service.softDelete(validEqCode));
    }

    @Test
    @DisplayName("Find Equipment using valid Barcode")
    void getByBarcode_usingValidBarcode_returnsEquipment() {
        String validBarcode = equipment0.getBarcode();
        Mockito.when(repository.findByBarcode(validBarcode)).thenReturn(Optional.of(equipment0));

        Equipment equipment = service.getByBarcode(validBarcode);

        assertNotNull(equipment);
        assertEquals(equipment0, equipment);
    }

    @Test
    @DisplayName("Find Equipment using invalid Barcode")
    void getByBarcode_usingInvalidBarcode_throwsException() {
        String invalidBarcode = equipment0.getBarcode();
        Mockito.when(repository.findByBarcode(invalidBarcode)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> service.getByBarcode(invalidBarcode));
    }

    @Test
    @DisplayName("Create correct Not Found string")
    void notFound_returnsNotFoundString () {
        String code = equipment0.getEquipmentCode();
        String notFoundString = "No equipment with code " + code + " was found";

        String notFoundResult = service.notFound(code);

        assertNotNull(notFoundResult);
        assertEquals(notFoundString, notFoundResult);
    }

    @Test
    @DisplayName("Create correct Already exist string")
    void alreadyExist_returnsAlreadyExistString () {
        String code = equipment0.getEquipmentCode();
        String alreadyExist = "Equipment with code " + code + " exist";

        String alreadyExistResult = service.alreadyExist(code);

        assertNotNull(alreadyExistResult);
        assertEquals(alreadyExist, alreadyExistResult);
    }

    @Test
    @DisplayName("Add or Update Equipments using same data with overwrite false")
    void addOrUpdate_withSameData_returnsZero() {
        List<Equipment> equipments = List.of(equipment0, equipment1);
        Mockito.when(repository.findByEquipmentCode(equipment0.getEquipmentCode())).thenReturn(Optional.of(equipment0));
        Mockito.when(repository.findByEquipmentCode(equipment1.getEquipmentCode())).thenReturn(Optional.of(equipment1));

        int itemsAffected = service.addOrUpdate(equipments, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update Equipments using different data with overwrite false")
    void addOrUpdate_withDifferentDataAndOverwriteFalse_returnsZero() {
        equipment0.setName("Updated name");
        List<Equipment> equipments = List.of(equipment0, equipment1);
        Mockito.when(repository.findByEquipmentCode(equipment0.getEquipmentCode())).thenReturn(Optional.of(equipment0));
        Mockito.when(repository.findByEquipmentCode(equipment1.getEquipmentCode())).thenReturn(Optional.of(equipment1));

        int itemsAffected = service.addOrUpdate(equipments, false);

        assertEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Add or Update Equipments using different data with overwrite true")
    void addOrUpdate_withDifferentDataAndOverwriteTrue_returnsNonZero() {
        Equipment updatedEq = new Equipment(equipment0.getEquipmentCode(), "Updated Name0", "Barcode0", EqStatus.GOOD, LocalDateTime.now(), true, false, false);
        List<Equipment> equipments = List.of(updatedEq, equipment1);
        Mockito.when(repository.findByEquipmentCode(equipment0.getEquipmentCode())).thenReturn(Optional.of(equipment0));
        Mockito.when(repository.findByEquipmentCode(equipment1.getEquipmentCode())).thenReturn(Optional.of(equipment1));

        int itemsAffected = service.addOrUpdate(equipments, true);

        assertNotEquals(0, itemsAffected);
    }

    @Test
    @DisplayName("Create Excel from a List of Equipments")
    void listToExcel_returnsExcelWithSameData() {
        try {
            equipment0.setId(0L);
            equipment1.setId(1L);
            List<Equipment> equipments = List.of(equipment0, equipment1);
            List<String> columns = List.of("ID", "Equipment code", "Name", "Barcode", "Status", "Defective since", "Is Duplicable?", "Is Borrowed?", "Delete Flag");

            ByteArrayInputStream inputStream = service.listToExcel(equipments);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);
            for (int i = 0; i < columns.size(); i++) {
                String columnName = headerRow.getCell(i).getStringCellValue();
                assertEquals(columns.get(i), columnName);
            }

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                Equipment equipment = equipments.get(i - 1);
                assertEquals(equipment.getId(),(long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                assertEquals(equipment.getEquipmentCode(), row.getCell(1).getStringCellValue());
                assertEquals(equipment.getName(), row.getCell(2).getStringCellValue());
                assertEquals(equipment.getBarcode(), row.getCell(3).getStringCellValue());
                assertEquals(equipment.getStatus(), EqStatus.of(row.getCell(4).getStringCellValue()));
                try {
                    assertEquals(equipment.getDefectiveSince(), LocalDateTime.parse(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()));
                } catch (DateTimeException ignored) {

                }
                assertEquals(equipment.getIsDuplicable(), row.getCell(6).getBooleanCellValue());
                assertEquals(equipment.getIsBorrowed(), row.getCell(7).getBooleanCellValue());
                assertEquals(equipment.getDeleteFlag(), row.getCell(8).getBooleanCellValue());
            }
        } catch (IOException ignored) {

        }
    }

    @Test
    @DisplayName("Create List of Equipments from Multipart file")
    void excelToList_returnsListOfAccount() {
        try {
            equipment0.setId(0L);
            equipment1.setId(1L);
            List<Equipment> equipments = List.of(equipment0, equipment1);
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Equipments");

            List<String> columns = List.of("ID", "Equipment code", "Name", "Barcode", "Status", "Defective since", "Is Duplicable?", "Is Borrowed?", "Delete Flag");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < equipments.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                Equipment eq = equipments.get(i);
                dataRow.createCell(0).setCellValue(eq.getId());
                dataRow.createCell(1).setCellValue(eq.getEquipmentCode());
                dataRow.createCell(2).setCellValue(eq.getName());
                dataRow.createCell(3).setCellValue(eq.getBarcode());
                dataRow.createCell(4).setCellValue(eq.getStatus().getName());
                if(eq.getDefectiveSince() != null) {
                    dataRow.createCell(5).setCellValue(eq.getDefectiveSince().toString());
                }
                dataRow.createCell(6).setCellValue(eq.getIsDuplicable());
                dataRow.createCell(7).setCellValue(eq.getIsBorrowed());
                dataRow.createCell(8).setCellValue(eq.getDeleteFlag());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            MultipartFile file = new MockMultipartFile("file", new ByteArrayInputStream(outputStream.toByteArray()));

            List<Equipment> equipmentsResult = service.excelToList(file);

            assertNotEquals(0, equipmentsResult.size());
            for (int i = 0; i < equipments.size(); i++) {
                Equipment equipmentExpected = equipments.get(i);
                Equipment equipmentResult = equipmentsResult.get(i);
                assertEquals(equipmentExpected, equipmentResult);
            }
        } catch (IOException ignored) {

        }
    }
}
