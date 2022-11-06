package com.eze.backend.restapi.service;

import com.eze.backend.restapi.dtos.YearLevelDto;
import com.eze.backend.restapi.enums.EqStatus;
import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.Account;
import com.eze.backend.restapi.model.Equipment;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.repository.YearLevelRepository;
import com.ibm.icu.text.RuleBasedNumberFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class YearLevelService implements IService<YearLevel>, IExcelService<YearLevel>{

    @Autowired
    private YearLevelRepository repository;

    @Override
    public List<YearLevel> getAll() {
        log.info("Fetching all YearLevel");
        return repository.findAll();
    }

    @Override
    public YearLevel get(Serializable yearNumber) {
        log.info("Fetching YearLevel with yearNumber {}", yearNumber);
        return repository.findByYearNumber(Integer.parseInt(yearNumber.toString()))
                .orElseThrow(() -> new ApiException(notFound(yearNumber), HttpStatus.NOT_FOUND));
    }

    @Override
    public YearLevel create(YearLevel yearLevel) {
        log.info("Creating YearLevel {}", yearLevel);
        Optional<YearLevel> opYL = repository.findByYearNumber(yearLevel.getYearNumber());
        if(opYL.isPresent()) throw new ApiException(alreadyExist(yearLevel.getYearNumber()), HttpStatus.BAD_REQUEST);
        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT);
        String yearName = nf.format(yearLevel.getYearNumber(), "%spellout-ordinal");
        String firstLetter = yearName.substring(0,1).toUpperCase();
        String remainingLetters = yearName.substring(1,yearName.length());
        yearLevel.setYearName(firstLetter + remainingLetters);
        yearLevel.setYearSections(new ArrayList<>());
        return repository.save(yearLevel);
    }

    // Rarely used, most likely
    @Override
    public YearLevel update(YearLevel yearLevel, Serializable yearNumber) {
        log.info("Updating YearLevel {} with yearNumber {}", yearLevel, yearNumber);
        YearLevel yearLevel1 = repository.findByYearNumber(Integer.parseInt(yearNumber.toString()))
                .orElseThrow(() -> new ApiException(notFound(yearNumber), HttpStatus.NOT_FOUND));
        yearLevel1.setYearNumber(yearLevel1.getYearNumber());
        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT);
        yearLevel1.setYearName(nf.format(yearLevel1.getYearNumber(), "%spellout-ordinal"));
        return repository.save(yearLevel1);
    }

    @Override
    public void delete(Serializable yearNumber) {
        log.info("Deleting YearLevel with yearNumber {}", yearNumber);
        YearLevel yearLevel = repository.findByYearNumber(Integer.parseInt(yearNumber.toString()))
                .orElseThrow(() -> new ApiException(notFound(yearNumber), HttpStatus.NOT_FOUND));
        repository.delete(yearLevel);
    }

    @Override
    public String notFound(Serializable yearNumber) {
        return "No YearLevel with year number " + yearNumber + " exist";
    }

    @Override
    public String alreadyExist(Serializable yearNumber) {
        return "YearLevel with year number " + yearNumber + " already exist";
    }

    @Override
    @Transactional
    public int addOrUpdate(List<YearLevel> yearLevels, boolean overwrite) {
        int itemsAffected = 0;
        for (YearLevel yl: yearLevels) {
            Optional<YearLevel> yearLevelOptional = repository.findByYearNumber(yl.getYearNumber());
            if(yearLevelOptional.isEmpty()){
                create(yl);
                itemsAffected++;
            }else{
                if(overwrite){
                    YearLevel oldYl = yearLevelOptional.get();
                    if(!oldYl.equals(yl)) {
                        update(yl, yl.getYearNumber());
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public ByteArrayInputStream listToExcel(List<YearLevel> yearLevels) {
        List<String> columnName = List.of("Year level", "Year name");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Year levels");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i=0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < yearLevels.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                YearLevel yl = yearLevels.get(i);
                dataRow.createCell(0).setCellValue(yl.getYearNumber());
                dataRow.createCell(1).setCellValue(yl.getYearName());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ApiException("Something went wrong when converting year levels to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<YearLevel> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            List<YearLevel> yearLevels = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                YearLevel yearLevel = new YearLevel();
                Row row = sheet.getRow(i);

                yearLevel.setYearNumber((int) row.getCell(0).getNumericCellValue());
                yearLevels.add(yearLevel);
            }
            return yearLevels;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Year levels", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
