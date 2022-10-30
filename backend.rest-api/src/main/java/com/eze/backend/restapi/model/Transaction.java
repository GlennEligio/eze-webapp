package com.eze.backend.restapi.model;

import com.eze.backend.restapi.dtos.TransactionDto;
import com.eze.backend.restapi.dtos.TransactionListDto;
import com.eze.backend.restapi.enums.TxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tx_eq",
            joinColumns = @JoinColumn(name = "tx_code_ref", referencedColumnName = "tx_code"),
            inverseJoinColumns = @JoinColumn(name = "eq_code_ref", referencedColumnName = "eq_code")
    )
    private List<Equipment> equipments;
    @ManyToOne
    @JoinColumn(name = "studentNumber", referencedColumnName = "studentNumber")
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

    public static TransactionListDto toTransactionListDto(Transaction transaction) {
        return new TransactionListDto(transaction.getTxCode(),
                transaction.getEquipments().size(),
                transaction.getBorrower().getFullName(),
                transaction.getBorrower().getYearAndSection().getSectionName(),
                transaction.getProfessor().getName(),
                transaction.getBorrowedAt() != null ? transaction.getBorrowedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a")) : null,
                transaction.getReturnedAt() != null ? transaction.getReturnedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a")) : null,
                transaction.getStatus());
    }

    public static TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(transaction.getTxCode(),
                transaction.getEquipments().stream().map(Equipment::toEquipmentDto).toList(),
                transaction.getBorrower().getFullName(),
                transaction.getBorrower().getYearAndSection().getSectionName(),
                transaction.getProfessor().getName(),
                transaction.getBorrowedAt(),
                transaction.getReturnedAt(),
                transaction.getStatus());
    }
}
