package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.EquipmentDto;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/equipments")
    public ResponseEntity<List<EquipmentDto>> getEquipments() {
        return ResponseEntity.ok(equipmentService.getAll().stream().map(Equipment::toEquipmentDto).toList());
    }

    @GetMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> getEquipment(@PathVariable("code") String code,
                                                     @RequestParam(required = false, defaultValue = "eqCode") String query) {
        if(query.equalsIgnoreCase("barcode")) {
            return ResponseEntity.ok(Equipment.toEquipmentDto(equipmentService.getByBarcode(code)));
        }
        return ResponseEntity.ok(Equipment.toEquipmentDto(equipmentService.get(code)));
    }

    @PostMapping("/equipments")
    public ResponseEntity<EquipmentDto> createEquipment(@Valid @RequestBody EquipmentDto equipmentDto) {
        Equipment newEq = equipmentService.create(Equipment.toEquipment(equipmentDto));
        return ResponseEntity.created(URI.create("/equipments/" + newEq.getId())).body(Equipment.toEquipmentDto(newEq));
    }

    @PutMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> updateEquipment(@PathVariable("code") String code,
                                                        @Valid @RequestBody EquipmentDto dto) {
        Equipment newEq = equipmentService.update(Equipment.toEquipment(dto), code);
        return ResponseEntity.ok(Equipment.toEquipmentDto(newEq));
    }

    @DeleteMapping("/equipments/{code}")
    public ResponseEntity<Object> deleteEquipment(@PathVariable("code") String code) {
        equipmentService.delete(code);
        return ResponseEntity.ok().build();
    }
}
