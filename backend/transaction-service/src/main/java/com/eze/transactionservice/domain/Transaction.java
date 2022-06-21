package com.eze.transactionservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Transaction {

    @Column(name = "trans_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Length(max = 24, min = 24)
    private String transactionCode;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trans_id")
    private List<TransactionItem> transactionItems;

    private String requestedBy;

    private String acceptedBy;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime dateCreated;

    private LocalDateTime dateResolved;

    private Boolean deleteFlag;

    public Transaction(String transactionId, List<TransactionItem> transactionItems, String requestedBy, String acceptedBy, Status status, LocalDateTime dateCreated, LocalDateTime dateResolved, Boolean deleteFlag) {
        this.transactionCode = transactionId;
        this.transactionItems = transactionItems;
        this.requestedBy = requestedBy;
        this.acceptedBy = acceptedBy;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dateResolved = dateResolved;
        this.deleteFlag = deleteFlag;
    }

    public Transaction(List<TransactionItem> transactionItems, String requestedBy, String acceptedBy, Status status, LocalDateTime dateCreated, LocalDateTime dateResolved, Boolean deleteFlag) {
        this.transactionItems = transactionItems;
        this.requestedBy = requestedBy;
        this.acceptedBy = acceptedBy;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dateResolved = dateResolved;
        this.deleteFlag = deleteFlag;
    }
}
