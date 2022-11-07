package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.repository.YearSectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class YearSectionService implements IService<YearSection>, IExcelService<YearSection>{

    @Autowired
    private YearSectionRepository repository;
    @Autowired
    private YearLevelService ylService;

    @Override
    public List<YearSection> getAll() {
        log.info("Fetching all YearSection");
        return repository.findAll();
    }

    @Override
    public YearSection get(Serializable sectionName) {
        log.info("Fetching YearSection with sectionName {}", sectionName);
        return repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
    }

    @Override
    public YearSection create(YearSection yearSection) {
        log.info("Creating YearSection {}", yearSection);
        if(yearSection.getYearLevel() == null && yearSection.getYearLevel().getYearNumber() == null) {
            throw new ApiException("YearSection must have YearLevel with correct yearName", HttpStatus.BAD_REQUEST);
        }
        YearLevel yearLevel = ylService.get(yearSection.getYearLevel().getYearNumber());
        yearSection.setYearLevel(yearLevel);
        return repository.save(yearSection);
    }

    // Will not be used most likely
    @Override
    public YearSection update(YearSection yearSection, Serializable sectionName) {
        log.info("Updating YearSection with sectionName {} and year level {}", sectionName, yearSection.getYearLevel().getYearNumber());
        YearSection ys = repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
        YearLevel yl = ylService.get(yearSection.getYearLevel().getYearNumber());
        ys.setYearLevel(yl);
        return repository.save(ys);
    }

    @Override
    public void delete(Serializable sectionName) {
        log.info("Deleting YearSection with sectionName {}", sectionName);
        YearSection yearSection = repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
        repository.delete(yearSection);
    }

    @Override
    public String notFound(Serializable sectionName) {
        return "No YearSection with sectionName " + sectionName + " was found";
    }

    @Override
    public String alreadyExist(Serializable sectionName) {
        return "YearSection with sectionName " + sectionName + " already exist";
    }

    @Override
    public int addOrUpdate(@Valid List<YearSection> yearSections, boolean overwrite) {
        int itemsAffected = 0;
        for (YearSection yearSection: yearSections) {
            Optional<YearSection> yearSectionOptional = repository.findBySectionName(yearSection.getSectionName());
            if(yearSectionOptional.isEmpty()){
                create(yearSection);
                itemsAffected++;
            }else{
                if(overwrite){
                    YearSection yearSection1 = yearSectionOptional.get();
                    yearSection.setId(yearSection1.getId());
                    if(!yearSection1.equals(yearSection)) {
                        update(yearSection, yearSection1.getSectionName());
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public ByteArrayInputStream listToExcel(List<YearSection> yearSections) {
        List<String> columnName = List.of("Section name", "Year level");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Year sections");

            // Creating header row
            Row row = sheet.createRow(0);
            for (int i=0; i < columnName.size(); i++) {
                row.createCell(i).setCellValue(columnName.get(i));
            }

            // Populating the Excel file with data
            for(int i=0; i < yearSections.size(); i++) {
                Row dataRow = sheet.createRow(i+1);
                YearSection yearSection = yearSections.get(i);
                dataRow.createCell(0).setCellValue(yearSection.getSectionName());
                dataRow.createCell(1).setCellValue(yearSection.getYearLevel().getYearNumber());
            }

            // Making size of the columns auto resize to fit data
            for(int i=0; i < columnName.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new ApiException("Something went wrong when converting year sections to excel file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<YearSection> excelToList(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            List<YearSection> yearSections = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                YearSection yearSection = new YearSection();
                Row row = sheet.getRow(i);

                yearSection.setSectionName(row.getCell(0).getStringCellValue());

                Integer yearNumber = (int) row.getCell(1).getNumericCellValue();
                YearLevel yearLevel = ylService.get(yearNumber);
                yearSection.setYearLevel(yearLevel);
                yearSections.add(yearSection);
            }
            return yearSections;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ApiException("Something went wrong when converting Excel file to Year sections", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
