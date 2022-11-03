package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.YearLevelDto;
import com.eze.backend.restapi.dtos.YearLevelWithSectionsDto;
import com.eze.backend.restapi.dtos.YearSectionWithYearLevelDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class YearLevel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(1)
    @Column(unique = true, nullable = false)
    private Integer yearNumber;
    @Column(unique = true, nullable = false)
    private String yearName;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "yearLevel", targetEntity = YearSection.class)
    private List<YearSection> yearSections;

    public static YearLevelDto toYearLevelDto(YearLevel yl) {
        YearLevelDto dto = new YearLevelDto();
        if(yl.getId() != null) dto.setId(yl.getId());
        if(yl.getYearName() != null) dto.setYearName(yl.getYearName());
        if(yl.getYearNumber() != null) dto.setYearNumber(yl.getYearNumber());
        return dto;
    }

    public static YearLevelWithSectionsDto toYearLevelWithSectionsDto (YearLevel yl) {
        return new YearLevelWithSectionsDto(yl.getId(), yl.getYearNumber(), yl.getYearName(), yl.getYearSections().stream().map(YearSection::toYearSectionDto).toList());
    }

    public static YearLevel toYearLevel(YearLevelDto dto) {
        YearLevel yl = new YearLevel();
        yl.setYearName(dto.getYearName());
        yl.setYearNumber(dto.getYearNumber());
        return yl;
    }
}
