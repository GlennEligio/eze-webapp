package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.repository.YearLevelRepository;
import com.ibm.icu.text.RuleBasedNumberFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class YearLevelService implements IService<YearLevel>{

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
}
