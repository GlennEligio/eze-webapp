package com.eze.backend.spring.service;

import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Professor;
import com.eze.backend.spring.repository.ProfessorRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.codecs.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProfessorService implements IService<Professor>, IExcelService<Professor>{

    private ProfessorRepository repository;
    private ObjectIdGenerator idGenerator;
    private AccountService accountService;

    public ProfessorService(ProfessorRepository repository, ObjectIdGenerator idGenerator, AccountService accountService) {
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.accountService = accountService;
    }

    @Override
    public List<Professor> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Professor> getAllNotDeleted() {
        log.info("Fetching all non deleted professors");
        return repository.findAllNotDeleted();
    }

    @Override
    public Professor get(Serializable name) {
        return repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
    }

    @Override
    public Professor create(Professor professor) {
        if(professor.getName() != null) {
            Optional<Professor> opProf = repository.findByName(professor.getName());
            if(opProf.isPresent()) {
                throw new ApiException(alreadyExist(professor.getName()), HttpStatus.BAD_REQUEST);
            }
        }
        // Creating account alongside the Student Creation
        Account account = new Account();
        account.setUsername(professor.getName());
        // TODO: Send this to the student's email via JavaMailSender
        String randomPassword = idGenerator.createId();
        account.setPassword(randomPassword);
        account.setEmail(professor.getEmail());
        account.setFullName(professor.getName());
        // TODO: Set a proper default image url
        account.setProfile(professor.getProfile());
        account.setType(AccountType.PROF);
        Account newAccount = accountService.create(account);
        professor.setProfessorAccount(newAccount);

        professor.setDeleteFlag(false);
        return repository.save(professor);
    }

    @Override
    public Professor update(Professor professor, Serializable name) {
        Professor professor1 = repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
        professor1.update(professor);
        return repository.save(professor1);
    }

    @Override
    public void delete(Serializable name) {
        Professor professor1 = repository.findByName(name.toString()).orElseThrow(() -> new ApiException(notFound(name), HttpStatus.NOT_FOUND));
        repository.delete(professor1);
    }

    @Override
    @Transactional
    public void softDelete(Serializable name) {
        log.info("Soft deleting professor");
        Optional<Professor> professorOptional = repository.findByName(name.toString());
        if(professorOptional.isEmpty()) {
            throw new ApiException(notFound(name), HttpStatus.NOT_FOUND);
        }
        Professor professor1 = professorOptional.get();
        if(Boolean.TRUE.equals(professor1.getDeleteFlag())) {
            throw new ApiException("Professor is already soft deleted", HttpStatus.BAD_REQUEST);
        }
        repository.softDelete(professor1.getName());
    }

    @Override
    public String notFound(Serializable name) {
        return "No professor with name " + name + " was found";
    }

    @Override
    public String alreadyExist(Serializable name) {
        return "Professor with name " + name + " already exist";
    }

    @Override
    @Transactional
    public int addOrUpdate(@Valid List<Professor> professors, boolean overwrite) {
        int itemsAffected = 0;
        for (Professor professor: professors) {
            Optional<Professor> professorOptional = repository.findByName(professor.getName());
            if(professorOptional.isEmpty()){
                repository.save(professor);
                itemsAffected++;
            }else{
                if(overwrite){
                    Professor oldProf = professorOptional.get();
                    professor.setId(oldProf.getId());
                    if(!oldProf.equals(professor)) {
                        oldProf.update(professor);
                        repository.save(oldProf);
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Professor> professors) {
        List<String> columnName = List.of("ID", "Name", "Contact Number", "Delete flag", "Email", "Profile image url");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Professor");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i=0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < professors.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                Professor prof = professors.get(i);
                dataRow.createCell(0).setCellValue(prof.getId());
                dataRow.createCell(1).setCellValue(prof.getName());
                dataRow.createCell(2).setCellValue(prof.getContactNumber());
                dataRow.createCell(3).setCellValue(prof.getDeleteFlag());
                dataRow.createCell(4).setCellValue(prof.getEmail());
                dataRow.createCell(5).setCellValue(prof.getProfile());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new ApiException("Something went wrong when converting professors to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Professor> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            List<Professor> professors = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Professor professor = new Professor();
                Row row = sheet.getRow(i);

                professor.setId((long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                professor.setName(row.getCell(1).getStringCellValue());
                professor.setContactNumber(row.getCell(2).getStringCellValue());
                professor.setDeleteFlag(row.getCell(3).getBooleanCellValue());
                professor.setEmail(row.getCell(4).getStringCellValue());
                professor.setProfile(row.getCell(5).getStringCellValue());
                professors.add(professor);
            }
            return professors;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Professors", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
