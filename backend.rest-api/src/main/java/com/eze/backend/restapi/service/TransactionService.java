package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.model.Transaction;
import com.eze.backend.restapi.repository.TransactionRepository;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements IService<Transaction> {

    private final TransactionRepository txRepo;
    private final EquipmentService eqService;
    private final ProfessorService profService;
    private final StudentService studentService;

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

        // Populate Professor and Student data of transaction
        transaction.setEquipments(equipments);

        String profName = transaction.getProfessor().getName();
        transaction.setProfessor(profService.get(profName));

        String studentNumber = transaction.getBorrower().getStudentNumber();
        transaction.setBorrower(studentService.get(studentNumber));

        // Set the transaction code
        transaction.setTxCode(new ObjectId().toHexString());

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
}
