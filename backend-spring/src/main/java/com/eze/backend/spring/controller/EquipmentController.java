package com.eze.backend.spring.controller;

import com.eze.backend.spring.dtos.EquipmentDto;
import com.eze.backend.spring.model.Equipment;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.service.EquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/equipments")
    public ResponseEntity<List<EquipmentDto>> getEquipments(@RequestParam(required = false) Boolean isBorrowed) {
        Stream<Equipment> equipments = equipmentService.getAllNotDeleted().stream();
        if(isBorrowed != null) {
            equipments = equipments.filter(e -> e.getIsBorrowed().equals(isBorrowed));
        }
        List<EquipmentDto> dtoResponse = equipments.map(Equipment::toEquipmentDto).toList();
        return ResponseEntity.ok(dtoResponse);
    }

    @GetMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> getEquipment(@PathVariable("code") String code,
                                                     @RequestParam(required = false, defaultValue = "eqCode") String query) {
        Equipment equipment = null;
        if (query.equalsIgnoreCase("barcode")) {
            equipment = equipmentService.getByBarcode(code);
        } else {
            equipment = equipmentService.get(code);
        }
        EquipmentDto dto = Equipment.toEquipmentDto(equipment);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/equipments/download")
    public void download(HttpServletResponse response) throws IOException {
        log.info("Preparing Item list for Download");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=equipments.xlsx");
        ByteArrayInputStream stream = equipmentService.listToExcel(equipmentService.getAll());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @PostMapping("/equipments/upload")
    public ResponseEntity<Object> upload(@RequestParam(required = false, defaultValue = "false") Boolean overwrite,
                                         @RequestParam MultipartFile file) {
        log.info("Preparing Excel for Equipment Database update");
        if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            throw new ApiException("Can only upload .xlsx files", HttpStatus.BAD_REQUEST);
        }
        List<Equipment> equipments = equipmentService.excelToList(file);
        log.info("Got the equipments from excel");
        int itemsAffected = equipmentService.addOrUpdate(equipments, overwrite);
        log.info("Successfully updated {} equipments database using the excel file", itemsAffected);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("Equipments Affected", itemsAffected);
        return ResponseEntity.ok(objectNode);
    }

    @PostMapping("/equipments")
    public ResponseEntity<EquipmentDto> createEquipment(@Valid @RequestBody EquipmentDto equipmentDto) {
        Equipment eqToCreate = Equipment.toEquipment(equipmentDto);
        Equipment newEq = equipmentService.create(eqToCreate);
        EquipmentDto responseDto = Equipment.toEquipmentDto(newEq);
        return ResponseEntity.created(URI.create("/api/v1/equipments/" + newEq.getId())).body(responseDto);
    }

    @PutMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> updateEquipment(@PathVariable("code") String code,
                                                        @Valid @RequestBody EquipmentDto dto) {
        Equipment eqToUpdate = Equipment.toEquipment(dto);
        Equipment newEq = equipmentService.update(eqToUpdate, code);
        EquipmentDto responseDto = Equipment.toEquipmentDto(newEq);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/equipments/{code}")
    public ResponseEntity<Object> deleteEquipment(@PathVariable("code") String code) {
        equipmentService.softDelete(code);
        return ResponseEntity.ok().build();
    }
}
