package com.eze.itemservice.dtos;

import com.eze.itemservice.domain.Category;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ItemDto {
    private String itemCode;
    private BigInteger currentAmount;
    private BigInteger totalAmount;
    private String description;
    private Category category;
    private Boolean deleteFlag;
}
