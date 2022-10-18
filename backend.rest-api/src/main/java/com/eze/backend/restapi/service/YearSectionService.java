package com.eze.backend.restapi.service;

import com.eze.backend.restapi.exception.ApiException;
import com.eze.backend.restapi.model.YearLevel;
import com.eze.backend.restapi.model.YearSection;
import com.eze.backend.restapi.repository.YearSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class YearSectionService implements IService<YearSection>{

    @Autowired
    private YearSectionRepository repository;
    @Autowired
    private YearLevelService yrService;

    @Override
    public List<YearSection> getAll() {
        return repository.findAll();
    }

    @Override
    public YearSection get(Serializable sectionName) {
        return repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
    }

    @Override
    public YearSection create(YearSection yearSection) {
        if(yearSection.getYearLevel() == null && yearSection.getYearLevel().getYearNumber() == null) {
            throw new ApiException("YearSection must have YearLevel with correct yearName", HttpStatus.BAD_REQUEST);
        }
        YearLevel yearLevel = yrService.get(yearSection.getYearLevel().getYearNumber());
        yearSection.setYearLevel(yearLevel);
        return repository.save(yearSection);
    }

    // Will not be used most likely
    @Override
    public YearSection update(YearSection yearSection, Serializable sectionName) {
        YearSection ys = repository.findBySectionName(sectionName.toString())
                .orElseThrow(() -> new ApiException(notFound(sectionName), HttpStatus.NOT_FOUND));
        YearLevel yl = yrService.get(yearSection.getYearLevel().getYearNumber());
        ys.setYearLevel(yl);
        return repository.save(ys);
    }

    @Override
    public void delete(Serializable sectionName) {
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
}
