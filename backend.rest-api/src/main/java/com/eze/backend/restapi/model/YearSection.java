package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.YearSectionDto;
import com.eze.backend.restapi.dtos.YearSectionWithYearLevelDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class YearSection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true, nullable = false)
    private String sectionName;
    @ManyToOne(optional = false)
    @JoinColumn(name = "yearLevel", referencedColumnName = "yearNumber")
    private YearLevel yearLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearSection that = (YearSection) o;
        return Objects.equals(id, that.id) && Objects.equals(sectionName, that.sectionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sectionName);
    }

    public static YearSectionDto toYearSectionDto(YearSection ys) {
        return new YearSectionDto(ys.getId(), ys.getSectionName());
    }

    public static YearSectionWithYearLevelDto toYearSectionWithYearLevelDto (YearSection ys) {
        return new YearSectionWithYearLevelDto(ys.getId(), ys.getSectionName(), YearLevel.toYearLevelDto(ys.getYearLevel()));
    }

    public static YearSection toYearSection(YearSectionWithYearLevelDto dto) {
        YearSection ys = new YearSection();
        ys.setSectionName(dto.getSectionName());
        ys.setYearLevel(YearLevel.toYearLevel(dto.getYearLevel()));
        return ys;
    }

    public static YearSection toYearSection(YearSectionDto dto) {
        YearSection ys = new YearSection();
        ys.setSectionName(dto.getSectionName());
        return ys;
    }
}
