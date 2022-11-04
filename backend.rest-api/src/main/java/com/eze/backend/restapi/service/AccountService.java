package com.eze.backend.restapi.service;

import com.eze.backend.restapi.dtos.EzeUserDetails;
import com.eze.backend.restapi.enums.AccountType;
import com.eze.backend.restapi.repository.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountService implements IService<Account>, IExcelService<Account>, UserDetailsService {

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, @Lazy PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Account> getAll() {
        log.info("Fetching all accounts");
        return repository.findAll();
    }

    @Override
    public Account get(Serializable username) {
        return repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
    }

    @Override
    public Account create(Account account) {
        log.info("Adding account: {}", account);
        if (account.getUsername() != null) {
            Optional<Account> accOp = repository.findByUsername(account.getUsername());
            if (accOp.isPresent()) {
                throw new ApiException(alreadyExist(account.getUsername()), HttpStatus.BAD_REQUEST);
            }
            account.setPassword(passwordEncoder.encode(account.getPassword()));
            account.setCreatedAt(LocalDateTime.now());
            account.setActive(true);
            return repository.save(account);
        }
        throw new ApiException("No username found in Account to create", HttpStatus.BAD_REQUEST);
    }

    @Override
    public Account update(Account account, Serializable username) {
        log.info("Updating account: {}", account);
        Account account1 = repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
        return repository.save(account1);
    }

    @Override
    public void delete(Serializable username) {
        log.info("Deleting account: {}", username);
        Account account = repository.findByUsername(username.toString())
                .orElseThrow(() -> new ApiException(notFound(username), HttpStatus.NOT_FOUND));
        repository.delete(account);
    }

    @Override
    public String notFound(Serializable username) {
        return "No account with account username " + username.toString() + " was found";
    }

    @Override
    public String alreadyExist(Serializable username) {
        return "Account with username " + username.toString() + " already exist";
    }

    @Override
    public int addOrUpdate(List<Account> accounts, boolean overwrite) {
        int itemsAffected = 0;
        for (Account account: accounts) {
            Optional<Account> accOp = repository.findByUsername(account.getUsername());
            if(accOp.isEmpty()){
                repository.save(account);
                itemsAffected++;
            }else{
                if(overwrite){
                    Account oldAcc = accOp.get();
                    if(!oldAcc.equals(account)) {
                        oldAcc.update(account);
                        repository.save(oldAcc);
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading account with username {}", username);
        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with username " + username));
        log.info("Account found: {}", account);
        return new EzeUserDetails(account);
    }

    @Override
    public ByteArrayInputStream listToExcel(List<Account> accounts) {
        List<String> columns = List.of("ID", "Full name", "Username", "Email", "Type", "Created At", "Is Active");
        try (Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Accounts");

            // Creating header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            // Populating the excel file
            for(int i = 0; i < accounts.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                dataRow.createCell(0).setCellValue(accounts.get(i).getId());
                dataRow.createCell(1).setCellValue(accounts.get(i).getFullName());
                dataRow.createCell(2).setCellValue(accounts.get(i).getUsername());
                dataRow.createCell(3).setCellValue(accounts.get(i).getEmail());
                dataRow.createCell(4).setCellValue(accounts.get(i).getType().getName());
                dataRow.createCell(5).setCellValue(accounts.get(i).getCreatedAt().toString());
                dataRow.createCell(6).setCellValue(accounts.get(i).getActive());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ApiException("Something went wrong with creating excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Account> excelToList(MultipartFile file) {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream());){
            List<Account> accounts = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Account account = new Account();
                Row row = sheet.getRow(i);

                account.setId((long) row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getNumericCellValue());
                account.setFullName(row.getCell(1).getStringCellValue());
                account.setUsername(row.getCell(2).getStringCellValue());
                account.setEmail(row.getCell(3).getStringCellValue());
                account.setType(AccountType.of(row.getCell(4).getStringCellValue()));
                account.setCreatedAt(LocalDateTime.parse(row.getCell(5).getStringCellValue()));
                account.setActive(row.getCell(6).getBooleanCellValue());
                accounts.add(account);
            }
            return accounts;
        }catch (IOException ex){
            throw new ApiException("Something went wrong when importing accounts", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
