package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.YearSectionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

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

    public static YearSectionDto toYearSectionDto(YearSection ys) {
        return new YearSectionDto(ys.getId(), ys.getSectionName());
    }
}
