package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.YearLevelDto;
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
        return new YearLevelDto(yl.getId(),
                yl.getYearNumber(),
                yl.getYearName(),
                yl.getYearSections().stream().map(YearSection::toYearSectionDto).toList());
    }
}
