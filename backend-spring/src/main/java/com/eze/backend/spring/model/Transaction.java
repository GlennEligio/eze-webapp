package com.eze.backend.spring.model;

import com.eze.backend.spring.dtos.*;
import com.eze.backend.spring.enums.TxStatus;
import com.eze.backend.spring.validation.EnumNamePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
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
    @Valid
    @NotNull(message = "Equipments must be present")
    private List<Equipment> equipments;

    @ManyToMany
    @JoinTable(
            name = "tx_eq_hist",
            joinColumns = @JoinColumn(name = "tx_code_ref_hist", referencedColumnName = "tx_code"),
            inverseJoinColumns = @JoinColumn(name = "eq_code_ref_hist", referencedColumnName = "eq_code")
    )
    @Valid
    @NotEmpty(message = "Equipments cant be empty")
    @NotNull(message = "Equipments must be present")
    private List<Equipment> equipmentsHist;

    @ManyToOne
    @JoinColumn(name = "studentNumber", referencedColumnName = "studentNumber")
    @NotNull(message = "Borrower must be present")
    @Valid
    private Student borrower;

    @ManyToOne
    @JoinColumn(name = "professor_name", referencedColumnName = "name")
    @NotNull(message = "Professor must be present")
    @Valid
    private Professor professor;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.ORDINAL)
    @EnumNamePattern(regexp = "^(PENDING|ACCEPTED|DENIED)", message = "Transaction's status can only either be 'PENDING', 'ACCEPTED', or 'DENIED'")
    private TxStatus status;
    private Boolean deleteFlag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(txCode, that.txCode) && Objects.equals(equipments, that.equipments) && Objects.equals(equipmentsHist, that.equipmentsHist) && Objects.equals(borrower, that.borrower) && Objects.equals(professor, that.professor) && Objects.equals(borrowedAt, that.borrowedAt) && Objects.equals(returnedAt, that.returnedAt) && status == that.status && Objects.equals(deleteFlag, that.deleteFlag);
    }

    public boolean customEquals(Transaction t) {
        boolean equals = true;
        if(!txCode.equals(t.getTxCode())) {
            log.info("TxCodes are not equals");
            equals = false;
        }

        if(!equipments.isEmpty() || !t.getEquipments().isEmpty()) {
            List<Equipment> sortedOldEq = equipments.stream().sorted(Comparator.comparing(Equipment::getEquipmentCode)).toList();
            List<Equipment> sortedNewEq = t.getEquipments().stream().sorted(Comparator.comparing(Equipment::getEquipmentCode)).toList();
            if(!sortedOldEq.equals(sortedNewEq)) {
                log.info("Equipments are not equal");
                equals = false;
            }
        }

        if(!equipmentsHist.isEmpty() || !t.getEquipmentsHist().isEmpty()) {
            List<Equipment> sortedOldEq = equipmentsHist.stream().sorted(Comparator.comparing(Equipment::getEquipmentCode)).toList();
            List<Equipment> sortedNewEq = t.getEquipmentsHist().stream().sorted(Comparator.comparing(Equipment::getEquipmentCode)).toList();
            if(!sortedOldEq.equals(sortedNewEq)) {
                log.info("Equipments hist are not equal");
                equals = false;
            }
        }

        if(!borrower.equals(t.getBorrower())) {
            log.info("Borrowers are not equal");
            equals = false;
        }
        if(!professor.equals(t.getProfessor())) {
            log.info("Professors are not equal");
            equals = false;
        }
        if(!borrowedAt.equals(t.getBorrowedAt())) {
            log.info("Borrowed at are not equals");
            equals = false;
        }
        if(returnedAt != null && t.getReturnedAt() != null && !returnedAt.equals(t.getReturnedAt())) {
            log.info("Returned at are not equals");
            equals = false;
        }
        if(returnedAt != null && !returnedAt.equals(t.getReturnedAt())) {
            log.info("Returned at are not equals");
            equals = false;
        }
        if(t.getReturnedAt() != null && !t.getReturnedAt().equals(returnedAt)) {
            log.info("Returned at are not equals");
            equals = false;
        }
        if(!status.getName().equals(t.getStatus().getName())) {
            log.info("Status is not equal");
            equals = false;
        }
        if(t.getDeleteFlag() != null && !t.getDeleteFlag().equals(deleteFlag)) {
            log.info("Delete flag is not equal");
            equals = false;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, txCode, equipments, equipmentsHist, borrower, professor, borrowedAt, returnedAt, status, deleteFlag);
    }

    public void update(Transaction newTransaction) {
        if(newTransaction.getEquipments() != null) {
            this.equipments = newTransaction.getEquipments();
        }
        if(newTransaction.getEquipmentsHist() != null) {
            this.equipmentsHist = newTransaction.getEquipmentsHist();
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
        if(newTransaction.getDeleteFlag() != null) {
            this.deleteFlag = newTransaction.getDeleteFlag();
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

    public static TransactionHistListDto toTransactionHistListDto(Transaction transaction) {
        return new TransactionHistListDto(transaction.getTxCode(),
                transaction.getEquipmentsHist().size(),
                transaction.getBorrower().getFullName(),
                transaction.getBorrower().getYearAndSection().getSectionName(),
                transaction.getProfessor().getName(),
                transaction.getBorrowedAt() != null ? transaction.getBorrowedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a")) : null,
                transaction.getReturnedAt() != null ? transaction.getReturnedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a")) : null,
                transaction.getStatus());
    }

    public static TransactionHistDto toTransactionHistDto(Transaction transaction) {
        return new TransactionHistDto(transaction.getTxCode(),
                transaction.getEquipmentsHist().stream().map(Equipment::toEquipmentDto).toList(),
                transaction.getBorrower().getFullName(),
                transaction.getBorrower().getYearAndSection().getSectionName(),
                transaction.getProfessor().getName(),
                transaction.getBorrowedAt(),
                transaction.getReturnedAt(),
                transaction.getStatus());
    }

    public static Transaction toTransaction (CreateUpdateTransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setEquipments(new ArrayList<>(dto.getEquipments().stream().map(Equipment::toEquipment).toList()));
        transaction.setBorrower(Student.toStudent(dto.getBorrower()));
        transaction.setProfessor(Professor.toProfessor(dto.getProfessor()));
        transaction.setStatus(TxStatus.valueOf(dto.getStatus()));
        return transaction;
    }
}
