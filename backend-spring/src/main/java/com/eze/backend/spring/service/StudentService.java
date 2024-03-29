package com.eze.backend.spring.service;

import com.eze.backend.spring.dtos.StudentListDto;
import com.eze.backend.spring.enums.AccountType;
import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.Account;
import com.eze.backend.spring.model.Student;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.StudentRepository;
import com.eze.backend.spring.util.ObjectIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentService implements IService<Student>, IExcelService<Student>{

    private final StudentRepository repository;
    private final YearLevelService ylService;
    private final YearSectionService ysService;
    private final AccountService accountService;
    private final ObjectIdGenerator idGenerator;
    private final EmailService emailService;

    public StudentService(StudentRepository repository, YearLevelService ylService, YearSectionService ysService, AccountService accountService, ObjectIdGenerator idGenerator, EmailService emailService) {
        this.repository = repository;
        this.ylService = ylService;
        this.ysService = ysService;
        this.accountService = accountService;
        this.idGenerator = idGenerator;
        this.emailService = emailService;
    }

    @Override
    public List<Student> getAll() {
        log.info("Fetching all students");
        return repository.findAll();
    }

    @Override
    public List<Student> getAllNotDeleted() {
        log.info("Fetching all non deleted students");
        return repository.findAllNotDeleted();
    }

    @Override
    public Student get(Serializable studentNo) {
        log.info("Fetching student with student number {}", studentNo);
        return repository.findByStudentNumber(studentNo.toString()).orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Student create(Student student) {
        log.info("Creating student {}", student);
        Optional<Student> studentOp = repository.findByStudentNumber(student.getStudentNumber());
        if(studentOp.isPresent()) {
            throw new ApiException(alreadyExist(student.getStudentNumber()), HttpStatus.BAD_REQUEST);
        }
        YearLevel yearLevel = ylService.get(student.getYearLevel().getYearNumber());
        YearSection yearSection = ysService.get(student.getYearAndSection().getSectionName());
        student.setYearLevel(yearLevel);
        student.setYearAndSection(yearSection);
        student.setDeleteFlag(false);

        // Creating account alongside the Student Creation
        Account account = new Account();
        account.setUsername(student.getStudentNumber());
        // TODO: Send this to the student's email via JavaMailSender
        String randomPassword = idGenerator.createId();
        account.setPassword(randomPassword);
        account.setEmail(student.getEmail());
        account.setFullName(student.getFullName());
        // TODO: Set a proper default image url
        account.setProfile(student.getProfile());
        account.setType(AccountType.STUDENT);
        Account newAccount = accountService.create(account);
        student.setStudentAccount(newAccount);

        Student student1 = repository.save(student);
        String emailMessage = "Hello " + student1.getFullName() + ",\n" +
                "Here is the password generated when your student data is created in the system: " + randomPassword + ".\n" +
                "Please login to the application and change your password immediately.\n\n";
        emailService.sendEmail(student1.getEmail(), "Intial Password for EZ-E account", emailMessage);

        return repository.save(student1);
    }

    @Override
    public Student update(Student student, Serializable studentNo) {
        log.info("Updating student {} with student number {}", student, studentNo);
        Student student1 = repository.findByStudentNumber(studentNo.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
        YearLevel yearLevel = ylService.get(student.getYearLevel().getYearNumber());
        YearSection yearSection = ysService.get(student.getYearAndSection().getSectionName());
        student.setYearLevel(yearLevel);
        student.setYearAndSection(yearSection);
        student1.update(student);
        return repository.save(student1);
    }

    @Override
    public void delete(Serializable studentNo) {
        log.info("Deleting student with student number {}", studentNo);
        Student student = repository.findByStudentNumber(studentNo.toString())
                .orElseThrow(() -> new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND));
        repository.delete(student);
    }

    @Override
    @Transactional
    public void softDelete(Serializable studentNo) {
        log.info("Soft deleting student with student number {}", studentNo);
        Optional<Student> studentOptional = repository.findByStudentNumber(studentNo.toString());
        if(studentOptional.isEmpty()) {
          throw new ApiException(notFound(studentNo), HttpStatus.NOT_FOUND);
        }
        Student student = studentOptional.get();
        if(student.getDeleteFlag()) {
            throw new ApiException("Student is already soft deleted", HttpStatus.BAD_REQUEST);
        }
        repository.softDelete(student.getStudentNumber());
    }

    @Override
    public String notFound(Serializable studentNo) {
        return "No student with student number " + studentNo + " was found";
    }

    @Override
    public String alreadyExist(Serializable studentNo) {
        return "Student with student number " + studentNo + " already exist";
    }

    @Transactional
    @Override
    public int addOrUpdate(@Valid List<Student> students, boolean overwrite) {
        int itemsAffected = 0;
        for (Student student: students) {
            Optional<Student> studentOptional = repository.findByStudentNumber(student.getStudentNumber());
            if(studentOptional.isEmpty()){
                create(student);
                itemsAffected++;
            }else{
                if(overwrite){
                    Student oldStudent = studentOptional.get();
                    if(!oldStudent.equals(student)){
                        oldStudent.update(student);
                        repository.save(oldStudent);
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Student> students) {
        List<String> columnName = List.of("ID", "Student number", "Full name", "Year and section", "Contact number", "Birthday", "Address", "Email", "Guardian", "Guardian number", "Year level", "Delete Flag", "Profile image url");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i=0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < students.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                StudentListDto student = Student.toStudentListDto(students.get(i));
                dataRow.createCell(0).setCellValue(student.getId());
                dataRow.createCell(1).setCellValue(student.getStudentNumber());
                dataRow.createCell(2).setCellValue(student.getFullName());
                dataRow.createCell(3).setCellValue(student.getYearAndSection());
                dataRow.createCell(4).setCellValue(student.getContactNumber());
                dataRow.createCell(11).setCellValue(students.get(i).getDeleteFlag());
                dataRow.createCell(12).setCellValue(students.get(i).getProfile());

                // Nullable properties check
                if(student.getBirthday() != null) dataRow.createCell(5).setCellValue(student.getBirthday());
                if(student.getAddress() != null) dataRow.createCell(6).setCellValue(student.getAddress());
                if(student.getEmail() != null) dataRow.createCell(7).setCellValue(student.getEmail());
                if(student.getGuardian() != null) dataRow.createCell(8).setCellValue(student.getGuardian());
                if(student.getGuardianNumber() != null) dataRow.createCell(9).setCellValue(student.getGuardianNumber());
                if(student.getYearLevel() != null) dataRow.createCell(10).setCellValue(student.getYearLevel());

            }

            // Making size of the column auto resize to fit data
            for(int i=0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ApiException("Something went wrong when converting students to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Student> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            List<Student> students = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Student student = new Student();
                Row row = sheet.getRow(i);

                student.setId((long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                student.setStudentNumber(row.getCell(1).getStringCellValue());
                student.setFullName(row.getCell(2).getStringCellValue());

                YearSection ys = ysService.get(row.getCell(3).getStringCellValue());
                student.setYearAndSection(ys);

                student.setContactNumber(row.getCell(4).getStringCellValue());
                student.setBirthday(row.getCell(5).getStringCellValue());
                student.setAddress(row.getCell(6).getStringCellValue());
                student.setEmail(row.getCell(7).getStringCellValue());
                student.setGuardian(row.getCell(8).getStringCellValue());
                student.setGuardianNumber(row.getCell(9).getStringCellValue());

                YearLevel yl = ylService.get((int) row.getCell(10).getNumericCellValue());
                student.setYearLevel(yl);

                student.setDeleteFlag(row.getCell(11).getBooleanCellValue());
                student.setProfile(row.getCell(12).getStringCellValue());
                students.add(student);
            }
            return students;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Equipments", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
