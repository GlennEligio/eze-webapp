package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.repository.EquipmentRepository;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService implements IService<Equipment>{

    private final EquipmentRepository repository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.repository = equipmentRepository;
    }

    @Override
    public List<Equipment> getAll() {
        return repository.findAll();
    }

    @Override
    public Equipment get(String code) {
        return repository.findByEquipmentCode(code).orElseThrow(() -> new ApiException("No equipment with code supplied was found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Equipment create(Equipment equipment) {
        Optional<Equipment> optionalEquipment = repository.findByEquipmentCode(equipment.getEquipmentCode());
        if(optionalEquipment.isPresent()) {
            throw new ApiException("Equipment with same code already exist", HttpStatus.BAD_REQUEST);
        }
        equipment.setEquipmentCode(new ObjectId().toHexString());
        return repository.save(equipment);
    }

    @Override
    public Equipment update(Equipment equipment, String code) {
        Equipment oldEquipment = repository.findByEquipmentCode(code).orElseThrow(() -> new ApiException("No transaction with id suppied was found", HttpStatus.NOT_FOUND));
        oldEquipment.update(equipment);
        return repository.save(oldEquipment);
    }

    @Override
    public void delete(String code) {
        Equipment equipment = repository.findByEquipmentCode(code).orElseThrow(() -> new ApiException("No transaction with id suppied was found", HttpStatus.NOT_FOUND));
        repository.delete(equipment);
    }
}
