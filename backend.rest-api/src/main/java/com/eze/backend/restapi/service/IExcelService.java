package com.eze.backend.restapi.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface IExcelService<T> {
    ByteArrayInputStream listToExcel(List<T> objects);
    List<T> excelToList(MultipartFile file);
}
