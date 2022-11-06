package com.eze.backend.restapi.service;

import com.eze.backend.restapi.enums.EqStatus;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.repository.EquipmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class EquipmentService implements IService<Equipment>, IExcelService<Equipment>{

    @Autowired
    private EquipmentRepository repository;

    @Override
    public List<Equipment> getAll() {
        log.info("Fetching all equipments");
        return repository.findAll();
    }

    @Override
    public Equipment get(Serializable code) {
        log.info("Fetching equipments with equipment code: {}", code);
        return repository.findByEquipmentCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Override
    public Equipment create(Equipment equipment) {
        log.info("Creating equipment {}", equipment);
        if(equipment.getEquipmentCode() != null) {
            Optional<Equipment> optionalEquipment = repository.findByEquipmentCode(equipment.getEquipmentCode());
            if(optionalEquipment.isPresent()) {
                throw new ApiException(alreadyExist(equipment.getEquipmentCode()), HttpStatus.BAD_REQUEST);
            }
        }
        equipment.setEquipmentCode(new ObjectId().toHexString());
        equipment.setIsBorrowed(false);
        return repository.save(equipment);
    }

    @Override
    public Equipment update(Equipment equipment, Serializable code) {
        log.info("Updating equipment with code: {}", code);
        Equipment oldEquipment = repository.findByEquipmentCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        oldEquipment.update(equipment);
        return repository.save(oldEquipment);
    }

    @Override
    public void delete(Serializable code) {
        log.info("Deleting equipment with code {}", code);
        Equipment equipment = repository.findByEquipmentCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        repository.delete(equipment);
    }

    @Override
    public String notFound(Serializable code) {
        return "No equipment with code " + code + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "Equipment with code " + code + " exist";
    }

    @Override
    @Transactional
    public int addOrUpdate(@Valid List<Equipment> equipments, boolean overwrite) {
        int itemsAffected = 0;
        for (Equipment equipment: equipments) {
            Optional<Equipment> eqOp = repository.findByEquipmentCode(equipment.getEquipmentCode());
            if(eqOp.isEmpty()){
                repository.save(equipment);
                itemsAffected++;
            }else{
                if(overwrite){
                    Equipment oldEq = eqOp.get();
                    if(!oldEq.equals(equipment)){
                        oldEq.update(equipment);
                        repository.save(oldEq);
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    public Equipment getByBarcode(String barcode) {
        log.info("Fetching equipment with barcode {}", barcode);
        return repository.findByBarcode(barcode)
                .orElseThrow(() -> new ApiException(notFound(barcode), HttpStatus.NOT_FOUND));
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Equipment> equipments) {
        List<String> columnName = List.of("ID", "Equipment code", "Name", "Barcode", "Status", "Defective since", "Is Duplicable?", "Is Borrowed?");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Equipments");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i=0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
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
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ApiException("Something went wrong when converting equipments to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Equipment> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            List<Equipment> equipments = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Equipment equipment = new Equipment();
                Row row = sheet.getRow(i);

                equipment.setId((long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                equipment.setEquipmentCode(row.getCell(1).getStringCellValue());
                equipment.setName(row.getCell(2).getStringCellValue());
                equipment.setBarcode(row.getCell(3).getStringCellValue());
                equipment.setStatus(EqStatus.of(row.getCell(4).getStringCellValue()));
                try {
                    equipment.setDefectiveSince(LocalDateTime.parse(row.getCell(5, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue()));
                } catch (DateTimeException ignored) {

                }
                equipment.setIsDuplicable(row.getCell(6).getBooleanCellValue());
                equipment.setIsBorrowed(row.getCell(7).getBooleanCellValue());
                equipments.add(equipment);
            }
            return equipments;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Equipments", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
