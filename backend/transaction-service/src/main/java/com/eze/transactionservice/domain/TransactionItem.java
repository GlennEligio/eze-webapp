package com.eze.transactionservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_item_id")
    private Long id;

    @Column(unique = true)
    private String transactionItemCode;

    private Long amount;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "item_id")
    private Item item;

    public TransactionItem(String transactionItemCode, Long amount, Item item) {
        this.transactionItemCode = transactionItemCode;
        this.amount = amount;
        this.item = item;
    }

    public TransactionItem(Long amount, Item item) {
        this.amount = amount;
        this.item = item;
    }
}
