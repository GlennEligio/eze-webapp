package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EquipmentService implements IService<Equipment>{

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

    public Equipment getByBarcode(String barcode) {
        log.info("Fetching equipment with barcode {}", barcode);
        return repository.findByBarcode(barcode)
                .orElseThrow(() -> new ApiException(notFound(barcode), HttpStatus.NOT_FOUND));
    }
}
