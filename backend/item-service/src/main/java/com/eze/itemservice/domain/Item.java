package com.eze.itemservice.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.math.BigInteger;
import java.util.List;

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

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;

    private Boolean deleteFlag;

    public Item(String itemCode, BigInteger currentAmount, BigInteger totalAmount, String description, Category category, Boolean deleteFlag) {
        this.itemCode = itemCode;
        this.currentAmount = currentAmount;
        this.totalAmount = totalAmount;
        this.description = description;
        this.category = category;
        this.deleteFlag = deleteFlag;
    }
}
