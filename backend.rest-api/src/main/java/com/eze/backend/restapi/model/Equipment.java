package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.EquipmentDto;
import com.eze.backend.restapi.enums.EqStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Equipment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Long id;
    @Column(unique = true, name = "eq_code", nullable = false)
    private String equipmentCode;
    private String name;
    @Column(unique = true)
    private String barcode;
    @Enumerated(EnumType.ORDINAL)
    private EqStatus status;
    private LocalDateTime defectiveSince;
    private Boolean isDuplicable;
    private Boolean isBorrowed;
    @ManyToMany(mappedBy = "equipments")
    // TODO: Temp fix for stackoverflow error, create DTO for this class that doesnt include this field
    @JsonIgnore
    private List<Transaction> transactions;

    public void update(Equipment newEquipment) {
        if(newEquipment.getEquipmentCode() != null) {
            this.equipmentCode = newEquipment.getEquipmentCode();
        }
        if(newEquipment.getBarcode() != null) {
            this.barcode = newEquipment.getBarcode();
        }
        if(newEquipment.getDefectiveSince() != null) {
            this.defectiveSince = newEquipment.getDefectiveSince();        }
        if(newEquipment.getName() != null) {
            this.name = newEquipment.getName();
        }
        if(newEquipment.getStatus() != null) {
            this.status = newEquipment.getStatus();
        }
        if(newEquipment.getIsDuplicable() != null) {
            this.isDuplicable = newEquipment.getIsDuplicable();
        }
        if(newEquipment.getIsBorrowed() != null) {
            this.isBorrowed = newEquipment.getIsBorrowed();
        }
    }

    public static EquipmentDto toEquipmentDto (Equipment equipment) {
        return new EquipmentDto(equipment.getId(),
                equipment.getEquipmentCode(),
                equipment.getName(),
                equipment.getBarcode(),
                equipment.getStatus().getName(),
                equipment.getDefectiveSince(),
                equipment.getIsDuplicable(),
                equipment.getIsBorrowed());
    }

    public static Equipment toEquipment(EquipmentDto dto) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentCode(dto.getEquipmentCode());
        equipment.setName(dto.getName());
        equipment.setBarcode(dto.getBarcode());
        equipment.setStatus(EqStatus.of(dto.getStatus()));
        equipment.setIsDuplicable(dto.getIsDuplicable());
        return equipment;
    }
}
