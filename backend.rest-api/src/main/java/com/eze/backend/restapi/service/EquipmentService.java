package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.repository.EquipmentRepository;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
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
    public Equipment get(Serializable code) {
        return repository.findByEquipmentCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Override
    public Equipment create(Equipment equipment) {
        if(equipment.getId() != null) {
            Optional<Equipment> optionalEquipment = repository.findByEquipmentCode(equipment.getEquipmentCode());
            if(optionalEquipment.isPresent()) {
                throw new ApiException(alreadyExist(equipment.getEquipmentCode()), HttpStatus.BAD_REQUEST);
            }
        }
        equipment.setEquipmentCode(new ObjectId().toHexString());
        return repository.save(equipment);
    }

    @Override
    public Equipment update(Equipment equipment, Serializable code) {
        Equipment oldEquipment = repository.findByEquipmentCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        oldEquipment.update(equipment);
        return repository.save(oldEquipment);
    }

    @Override
    public void delete(Serializable code) {
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
}
