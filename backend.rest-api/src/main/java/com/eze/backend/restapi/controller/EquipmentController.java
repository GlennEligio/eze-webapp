package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.service.EquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService service) {
        this.equipmentService = service;
    }

    @GetMapping("/equipments")
    public ResponseEntity<List<Equipment>> getEquipments() {
        return ResponseEntity.ok(equipmentService.getAll());
    }

    @GetMapping("/equipments/{code}")
    public ResponseEntity<Equipment> getEquipment(@PathVariable("code") String code) {
        return ResponseEntity.ok(equipmentService.get(code));
    }

    @PostMapping("/equipments")
    public ResponseEntity<Equipment> createEquipment(@RequestBody Equipment equipment) {
        Equipment newEq = equipmentService.create(equipment);
        return ResponseEntity.created(URI.create("/equipments/" + newEq.getEquipmentCode())).body(newEq);
    }

    @PutMapping("/equipments/{code}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable("code") String code, @RequestBody Equipment equipment) {
        Equipment newEq = equipmentService.update(equipment, code);
        return ResponseEntity.ok(newEq);
    }

    @DeleteMapping("/equipments/{code}")
    public ResponseEntity<Object> deleteEquipment(@PathVariable("code") String code) {
        equipmentService.delete(code);
        return ResponseEntity.ok().build();
    }
}
