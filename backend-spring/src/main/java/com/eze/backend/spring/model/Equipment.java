package com.eze.backend.spring.model;

import com.eze.backend.spring.dtos.EquipmentDto;
import com.eze.backend.spring.enums.EqStatus;
import com.eze.backend.spring.validation.EnumNamePattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Equipment's name can't be blank")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Equipment's barcode can't be blank")
    private String barcode;

    @Enumerated(EnumType.ORDINAL)
    @EnumNamePattern(regexp = "^(DEFECTIVE|GOOD)", message = "Equipment's status can only either be 'DEFECTIVE' or 'GOOD'")
    private EqStatus status;
    private LocalDateTime defectiveSince;

    @NotNull(message = "Equipment must be defined to be either duplicable or not")
    private Boolean isDuplicable;
    private Boolean isBorrowed;
    private Boolean deleteFlag;
    @ManyToMany(mappedBy = "equipments")
    // TODO: Temp fix for stackoverflow error, create DTO for this class that doesnt include this field
    @JsonIgnore
    private List<Transaction> transactions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(id, equipment.id) && Objects.equals(equipmentCode, equipment.equipmentCode) && Objects.equals(name, equipment.name) && Objects.equals(barcode, equipment.barcode) && status == equipment.status && Objects.equals(defectiveSince, equipment.defectiveSince) && Objects.equals(isDuplicable, equipment.isDuplicable) && Objects.equals(isBorrowed, equipment.isBorrowed) && Objects.equals(deleteFlag, equipment.deleteFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, equipmentCode, name, barcode, status, defectiveSince, isDuplicable, isBorrowed, deleteFlag);
    }

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
        if(newEquipment.getDeleteFlag() != null) {
            this.deleteFlag=newEquipment.getDeleteFlag();
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
        equipment.setIsBorrowed(dto.getIsBorrowed());
        return equipment;
    }
}
