package com.eze.backend.restapi.controller;

import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.service.EquipmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EquipmentController {

    private EquipmentService service;

    public EquipmentController(EquipmentService service) {
        this.service = service;
    }

    @GetMapping("/equipments")
    public List<Equipment> getEquipments() {
        return service.getAll();
    }
}
