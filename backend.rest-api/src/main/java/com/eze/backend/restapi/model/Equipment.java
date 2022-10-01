package com.eze.backend.restapi.model;

import com.eze.backend.restapi.enums.EqStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String equipmentCode;
    private String name;
    @Column(unique = true)
    private String barcode;
    // will be EquipmentStatus enum later on
    @Enumerated(EnumType.ORDINAL)
    private EqStatus status;
    private LocalDateTime defectiveSince;

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
    }
}
