package com.eze.backend.spring.model;

import com.eze.backend.spring.dtos.ProfessorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Professor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "professor_id")
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Professor name can't be blank")
    private String name;

    @NotBlank(message = "Professor contact number can't be blank")
    @Pattern(regexp = "^(09|\\+639)\\d{9}$", message = "Professor contact number must be a valid PH mobile number")
    private String contactNumber;

    private Boolean deleteFlag;

    @OneToMany(mappedBy = "professor")
    private List<Transaction> transactions;

    public Professor(String name, String contactNumber, Boolean deleteFlag) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.deleteFlag = deleteFlag;
    }

    public void update(Professor newProf) {
        if(newProf.getContactNumber() != null) {
            this.contactNumber = newProf.getContactNumber();
        }
        if(newProf.getDeleteFlag() != null) {
            this.deleteFlag = newProf.getDeleteFlag();
        }
    }

    public static ProfessorDto toProfessorDto(Professor professor) {
        return new ProfessorDto(professor.getId(), professor.getName(), professor.getContactNumber());
    }

    public static Professor toProfessor(ProfessorDto dto) {
        Professor professor = new Professor();
        professor.setName(dto.getName());
        professor.setContactNumber(dto.getContactNumber());
        return professor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return Objects.equals(id, professor.id) && Objects.equals(name, professor.name) && Objects.equals(contactNumber, professor.contactNumber) && Objects.equals(deleteFlag, professor.deleteFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contactNumber, deleteFlag);
    }
}