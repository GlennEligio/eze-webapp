package com.eze.backend.restapi.service;

import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService implements IService<Equipment, Long>{

    private EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<Equipment> getAll() {
        return equipmentRepository.findAll();
    }

    @Override
    public Equipment get(Long aLong) {
        return null;
    }

    @Override
    public void update(Equipment entity) {

    }

    @Override
    public void delete(Long aLong) {

    }
}
