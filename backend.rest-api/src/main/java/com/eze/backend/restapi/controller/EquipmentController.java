package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.dtos.EquipmentDto;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public ResponseEntity<List<EquipmentDto>> getEquipments() {
        return ResponseEntity.ok(equipmentService.getAll().stream().map(Equipment::toEquipmentDto).toList());
    }

    @GetMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> getEquipment(@PathVariable("code") String code) {
        return ResponseEntity.ok(Equipment.toEquipmentDto(equipmentService.get(code)));
    }

    @PostMapping("/equipments")
    public ResponseEntity<EquipmentDto> createEquipment(@RequestBody Equipment equipment) {
        Equipment newEq = equipmentService.create(equipment);
        return ResponseEntity.created(URI.create("/equipments/" + newEq.getId())).body(Equipment.toEquipmentDto(newEq));
    }

    @PutMapping("/equipments/{code}")
    public ResponseEntity<EquipmentDto> updateEquipment(@PathVariable("code") String code, @RequestBody Equipment equipment) {
        Equipment newEq = equipmentService.update(equipment, code);
        return ResponseEntity.ok(Equipment.toEquipmentDto(newEq));
    }

    @DeleteMapping("/equipments/{code}")
    public ResponseEntity<Object> deleteEquipment(@PathVariable("code") String code) {
        equipmentService.delete(code);
        return ResponseEntity.ok().build();
    }
}
