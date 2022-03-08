package com.example.demo.model;


import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transaction_log")
public class TransactionLog {

    @Column(name="transaction_id")
    @Id
    private int transactionId;

    @ManyToOne(targetEntity = TransactionType.class)
    @JoinColumn(name="transaction_type_id")
    private Integer transactionTypeId; //TODO Integer or TransactionType ??

    @Column(name="transaction_date")
    private Date transaction_date;

    private String iban;
    // TODO foreign key

    @Column(name="Recipient_iban")
    private String recipientIban;
    // TODO foreign key

    @Column(name="transaction_amount")
    private double transactionAmount;

    @Column(name="currency_id_used")
    private int currencyIdUsed;
    // TODO foreign key

    @Column(name="currency_id_recipient")
    private int currencyIdRecipient;
    // TODO foreign key

}
