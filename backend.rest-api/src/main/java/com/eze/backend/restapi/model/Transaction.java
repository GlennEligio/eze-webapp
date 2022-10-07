package com.eze.backend.restapi.model;

import com.eze.backend.restapi.enums.TxStatus;
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
@Table(name = "transaction_table")
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_id")
    private Long id;
    @Column(unique = true, nullable = false, name = "tx_code")
    private String txCode;
    @ManyToMany
    @JoinTable(
            name = "tx_eq",
            joinColumns = @JoinColumn(name = "tx_code_ref", referencedColumnName = "tx_code"),
            inverseJoinColumns = @JoinColumn(name = "eq_code_ref", referencedColumnName = "eq_code")
    )
    private List<Equipment> equipments;
    @ManyToOne
    @JoinColumn(name = "student_number", referencedColumnName = "student_number")
    private Student borrower;
    @ManyToOne
    @JoinColumn(name = "professor_name", referencedColumnName = "name")
    private Professor professor;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    @Enumerated(EnumType.ORDINAL)
    private TxStatus status;

    public void update(Transaction newTransaction) {
        if(newTransaction.getEquipments() != null) {
            this.equipments = newTransaction.getEquipments();
        }
        if(newTransaction.getBorrower() != null) {
            this.borrower = newTransaction.getBorrower();
        }
        if(newTransaction.getProfessor() != null) {
            this.professor = newTransaction.getProfessor();
        }
        if(newTransaction.getBorrowedAt() != null) {
            this.borrowedAt = newTransaction.getBorrowedAt();
        }
        if(newTransaction.getReturnedAt() != null) {
            this.returnedAt = newTransaction.getReturnedAt();
        }
        if(newTransaction.getStatus() != null) {
            this.status = newTransaction.getStatus();
        }

    }
}
