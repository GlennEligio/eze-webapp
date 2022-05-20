package com.eze.transactionservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @Column(name = "trans_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trans_id")
    private List<TransactionItem> transactionItems;

    private String requestedBy;

    private String acceptedBy;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime dateCreated;

    private LocalDateTime dateResolved;

    private Boolean deleteFlag = false;

    public Transaction(String transactionId, List<TransactionItem> transactionItems, String requestedBy, String acceptedBy, Status status, LocalDateTime dateCreated, LocalDateTime dateResolved) {
        this.transactionId = transactionId;
        this.transactionItems = transactionItems;
        this.requestedBy = requestedBy;
        this.acceptedBy = acceptedBy;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dateResolved = dateResolved;
    }
}
