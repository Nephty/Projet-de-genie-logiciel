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

    @ManyToOne
    @JoinColumn(
            name="transaction_type_id",
            referencedColumnName = "transaction_type_id"
    )
    private TransactionType transactionTypeId;

    @Column(name="transaction_date")
    private Date transaction_date;

    /*
    @ManyToMany
    @JoinColumn(
            name = "iban",
            referencedColumnName = "iban"
    )
    private Account iban;
    */

    
    @Column
    private String iban;
    // TODO : WHY NOT WORKING


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
