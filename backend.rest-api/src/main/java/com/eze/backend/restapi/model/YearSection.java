package com.eze.backend.restapi.model;

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
}
