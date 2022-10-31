package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.model.Transaction;
import com.eze.backend.restapi.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TransactionService implements IService<Transaction> {

    private final TransactionRepository txRepo;
    private final EquipmentService eqService;
    private final ProfessorService profService;
    private final StudentService studentService;

    @Autowired
    public TransactionService(TransactionRepository txRepo, EquipmentService eqService, ProfessorService profService, StudentService studentService) {
        this.txRepo = txRepo;
        this.eqService = eqService;
        this.profService = profService;
        this.studentService = studentService;
    }

    @Override
    public List<Transaction> getAll() {
        return txRepo.findAll();
    }

    @Override
    public Transaction get(Serializable code) {
        return txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Override
    public Transaction create(Transaction transaction) {
        if (transaction.getTxCode() != null) {
            Optional<Transaction> opTx = txRepo.findByTxCode(transaction.getTxCode());
            if (opTx.isPresent()) {
                throw new ApiException(alreadyExist(transaction.getTxCode()), HttpStatus.BAD_REQUEST);
            }
        }
        // Populate transaction's equipments
        List<Equipment> equipments = transaction.getEquipments().parallelStream()
                .map(eq -> eqService.get(eq.getEquipmentCode()))
                .toList();

        // Check if any equipments is not duplicable and is already borrowed
        checkEqAlreadyBorrowed(equipments);

        // Set the isBorrowed of non-duplicable equipment to true
        equipments.forEach(e -> {
            if(Boolean.FALSE.equals(e.getIsDuplicable())) {
                e.setIsBorrowed(true);
            }
            eqService.update(e, e.getEquipmentCode());
        });

        // Populate Equipment and Equipment history data of transaction
        transaction.setEquipments(new ArrayList<>(equipments));
        transaction.setEquipmentsHist(new ArrayList<>(equipments));

        // Populate Professor and Student data of transaction
        String profName = transaction.getProfessor().getName();
        transaction.setProfessor(profService.get(profName));

        String studentNumber = transaction.getBorrower().getStudentNumber();
        transaction.setBorrower(studentService.get(studentNumber));

        // Set the transaction code
        transaction.setTxCode(new ObjectId().toHexString());
        transaction.setBorrowedAt(LocalDateTime.now());

        return txRepo.save(transaction);
    }

    @Transactional
    @Override
    public Transaction update(Transaction transaction, Serializable code) {
        Transaction tx = txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));

        if (transaction.getEquipments() != null) {
            List<Equipment> eqs = transaction.getEquipments().parallelStream()
                    .map(eq -> eqService.get(eq.getEquipmentCode()))
                    .toList();

            checkEqAlreadyBorrowed(eqs);
            transaction.setEquipments(eqs);
        }

        if (transaction.getProfessor() != null) {
            String profName = transaction.getProfessor().getName();
            transaction.setProfessor(profService.get(profName));
        }

        if (transaction.getBorrower() != null) {
            String studentNumber = transaction.getBorrower().getStudentNumber();
            transaction.setBorrower(studentService.get(studentNumber));
        }

        tx.update(transaction);

        return txRepo.save(tx);
    }

    @Override
    public void delete(Serializable code) {
        Transaction transaction = txRepo.findByTxCode(code.toString()).orElseThrow(() -> new ApiException(notFound(code), HttpStatus.NOT_FOUND));
        txRepo.delete(transaction);
    }

    @Override
    public String notFound(Serializable code) {
        return "No transaction with code " + code + " was found";
    }

    @Override
    public String alreadyExist(Serializable code) {
        return "Transaction with code " + code + " already exist";
    }

    private String eqBorrowed(List<Equipment> equipments) {
        String equipmentIds = equipments.stream()
                .map(Equipment::getEquipmentCode)
                .reduce("", (ids, eqId) -> ids + ", " + eqId);
        return "The following equipments is already borrowed: " + equipmentIds;
    }

    private void checkEqAlreadyBorrowed(List<Equipment> equipments) {
        // Check if any equipments is not duplicable and is already borrowed
        List<Equipment> eqAlreadyBorrowed = equipments.parallelStream()
                .filter(eq -> !eq.getIsDuplicable())
                .filter(Equipment::getIsBorrowed)
                .toList();

        // If there is such equipment, list them and show them in response error
        if (!eqAlreadyBorrowed.isEmpty()) {
            throw new ApiException(eqBorrowed(eqAlreadyBorrowed), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public Transaction returnEquipments(String borrower, String professor, List<String> equipmentsBarcode) {
        log.info("Returning equipments with barcodes {}, for borrower {}, and professor {}", equipmentsBarcode, borrower, professor);
        List<Equipment> eqs = new ArrayList<>();
        for(String barcode: equipmentsBarcode) {
            eqs.add(eqService.getByBarcode(barcode));
        }
        log.info("Equipments found: {}", eqs.stream().map(Equipment::toEquipmentDto).toList());
        // Filter transactions so only those that match the borrower, professor, and contains all equipments from list of barcodes
        Transaction transactionMatch = getAll().stream()
                .filter(t -> t.getBorrower().getStudentNumber().equalsIgnoreCase(borrower)
                        && t.getProfessor().getName().equalsIgnoreCase(professor)
                        && t.getReturnedAt() == null)
                .filter(t -> t.getEquipments().containsAll(eqs))
                .findFirst()
                .orElseThrow(() -> new ApiException(String.format("No transaction found that matches the following: Borrower %s, Professor %s, Barcodes %s", borrower, professor, equipmentsBarcode), HttpStatus.NOT_FOUND));

        log.info("Got the matching transaction {}", Transaction.toTransactionListDto(transactionMatch));
        // Filter new transaction equipments so that only those that is NOT included in equipments return is left
        List<Equipment> newEqs = new ArrayList<>(transactionMatch.getEquipments().stream()
                .filter(e -> !eqs.contains(e)).toList());
        log.info("Removed the returned eqs in the transaction");
        // Also change the non-duplicable equipments isBorrowed status to false
        eqs.forEach(e -> {
            if(Boolean.FALSE.equals(e.getIsDuplicable())) {
                e.setIsBorrowed(false);
            }
            eqService.update(e, e.getEquipmentCode());
        });
        log.info("Updated the equipments isBorrowed");

        // Set the transaction's new equipments list
        transactionMatch.setEquipments(newEqs);
        log.info("Updated the transaction equipments");
        // Change the returnedAt value if newEqs is empty
        if(newEqs.isEmpty()) transactionMatch.setReturnedAt(LocalDateTime.now());
        log.info("Update the transaction returnedAt value");
        log.info("New transaction {}", Transaction.toTransactionDto(transactionMatch));
        return txRepo.save(transactionMatch);
    }
}
