package com.example.demo.model;


import com.example.demo.model.CompositePK.TransactionLogPK;
import com.example.demo.request.TransactionReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;


/**
 * The model for the table transaction_log.
 * This class as a composite primary key designed with an idClass {@link TransactionLogPK} <br>
 * Each transaction have 2 lines in the DB (in/out ways).
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TransactionLogPK.class)
@Entity
@Table(name="transaction_log")
public class TransactionLog {

    @Id
    @Column(
            name="transaction_id",
            updatable = false
    )
    private Integer transactionId;

    @Id
    @Column(
            name = "is_sender",
            nullable = false
    )
    private Boolean isSender;

    @ManyToOne
    @JoinColumn(
            name="transaction_type_id",
            referencedColumnName = "transaction_type_id"
    )
    private TransactionType transactionTypeId;

    @Column(name="transaction_date")
    private Date transactionDate;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "iban", referencedColumnName = "iban"),
            @JoinColumn(name = "currency_id", referencedColumnName = "currency_type_id")
    })
    private SubAccount subAccount;
    
    @Column(
            name="transaction_amount",
            nullable = false
    )
    private Double transactionAmount;

    @Column(
            nullable = false
    )
    private Boolean processed;

    @Column(
            nullable = false
    )
    private String comments;

    /**
     * Custom constructor for TransactionLog with the custom Request. <br>
     * IF the date is null, set the transaction to the current date.
     * @param transactionReq The custom request for creating/modifying a TransactionLog
     */
    public TransactionLog(TransactionReq transactionReq) {
        Date now = new Date(System.currentTimeMillis());
        // setting to current time if the date is not provided or if it's from before to not allow forgery
        // of date execution
        if(transactionReq.getTransactionDate() == null || transactionReq.getTransactionDate().before(now)) {
            transactionDate = now;
        } else {
            transactionDate = transactionReq.getTransactionDate();
        }
        // if it's null then we default it to false
        processed = transactionReq.getProcessed() != null && transactionReq.getProcessed();

        comments = transactionReq.getComments();

        transactionAmount = transactionReq.getTransactionAmount();
    }

    /**
     * Get the Transaction fee.
     * @return the fee of the transaction
     */
    @JsonIgnore
    public double getFee() {
        if(!isSender) {
            return 0;
        }
        return transactionAmount * transactionTypeId.getTransactionFee();
    }
}
