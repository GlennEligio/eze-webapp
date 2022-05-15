package com.eze.itemservice.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String itemCode;

    @Positive
    private BigInteger currentAmount;

    @Positive
    private BigInteger totalAmount;

    private String description;

    private Boolean deleteFlag;

    public Item(String itemCode, BigInteger currentAmount, BigInteger totalAmount, String description) {
        this.itemCode = itemCode;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
        this.description = description;
    }
}
