package com.example.demo.model;


import lombok.*;

import javax.persistence.*;
import java.sql.Date;

import static javax.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transaction_log")
public class TransactionLog {
    /*
    This class is kinda weird, trying to set the many-to-many relationship
    TODO : see if the table that join the subAccounts and the Transaction is created automatically.
    TODO : check for all the foreign keys in the table.
     */

    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1 // How much will the sequence increase from
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "transaction_sequence"
    )
    @Column(
            name="transaction_id",
            updatable = false
    )
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(
            name="transaction_type_id",
            referencedColumnName = "transaction_type_id"
    )
    private TransactionType transactionTypeId;

    @Column(name="transaction_date")
    private Date transaction_date;

    @Column
    private String iban;
    // TODO : WHY NOT WORKING


    @Column(name="Recipient_iban")
    private String recipientIban;
    // TODO foreign key

    @Column(
            name="transaction_amount",
            nullable = false
    )
    private Double transactionAmount;

    @Column(name="currency_id_used")
    private Integer currencyIdUsed;
    // TODO foreign key

    @Column(name="currency_id_recipient")
    private Integer currencyIdRecipient;
    // TODO foreign key


    public TransactionLog(TransactionType transactionTypeId, Date transaction_date, String iban,
                          String recipientIban, Double transactionAmount,
                          Integer currencyIdUsed, Integer currencyIdRecipient) {
        this.transactionTypeId = transactionTypeId;
        this.transaction_date = transaction_date;
        this.iban = iban;
        this.recipientIban = recipientIban;
        this.transactionAmount = transactionAmount;
        this.currencyIdUsed = currencyIdUsed;
        this.currencyIdRecipient = currencyIdRecipient;
    }
}
