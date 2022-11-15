package com.eze.backend.spring.service;

import com.eze.backend.spring.exception.ApiException;
import com.eze.backend.spring.model.YearLevel;
import com.eze.backend.spring.model.YearSection;
import com.eze.backend.spring.repository.YearSectionRepository;
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
    public List<YearSection> getAllNotDeleted() {
        log.info("Fetching all non deleted YearSections");
        return repository.findAllNotDeleted();
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
        if(yearSection.getSectionName() != null) {
            Optional<YearSection> yearSection1 = repository.findBySectionName(yearSection.getSectionName());
            if(yearSection1.isPresent()) {
                throw new ApiException(alreadyExist(yearSection.getSectionName()), HttpStatus.BAD_REQUEST);
            }
        }
        if(null == yearSection.getYearLevel()) {
            throw new ApiException("YearSection must have YearLevel", HttpStatus.BAD_REQUEST);
        }
        YearLevel yearLevel = ylService.get(yearSection.getYearLevel().getYearNumber());
        yearSection.setYearLevel(yearLevel);
        yearSection.setDeleteFlag(false);
        return repository.save(yearSection);
    }

    // Will not be used in FRE
    @Override
    public YearSection update(YearSection yearSection, Serializable sectionName) {
        log.info("Updating YearSection with sectionName {} and year level {}", sectionName, yearSection.getYearLevel().getYearNumber());
        YearSection ys = repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
        YearLevel yl = ylService.get(yearSection.getYearLevel().getYearNumber());
        ys.setYearLevel(yl);
        ys.setDeleteFlag(yearSection.getDeleteFlag());
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
    @Transactional
    public void softDelete(Serializable sectionName) {
        log.info("Soft deleting YearSection with sectionName {}", sectionName);
        Optional<YearSection> yearSectionOptional = repository.findBySectionName(sectionName.toString());
        if(yearSectionOptional.isEmpty()) {
            throw new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND);
        }
        YearSection yearSection = yearSectionOptional.get();
        if(Boolean.TRUE.equals(yearSection.getDeleteFlag())) {
            throw new ApiException("Year section is already soft deleted", HttpStatus.BAD_REQUEST);
        }
        repository.softDelete(yearSection.getSectionName());
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
    @Transactional
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
                    log.info(yearSection1.toString());
                    log.info(yearSection.toString());
                    if(!yearSection1.equals(yearSection)) {
                        update(yearSection, yearSection1.getSectionName());
                        itemsAffected++;
                    }
                }
            }
        }
        return itemsAffected;
    }

    @Override
    public ByteArrayInputStream listToExcel(List<YearSection> yearSections) {
        List<String> columnName = List.of("Section name", "Year level", "Delete flag");
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
                dataRow.createCell(2).setCellValue(yearSection.getDeleteFlag());
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

                yearSection.setDeleteFlag(row.getCell(2).getBooleanCellValue());
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
