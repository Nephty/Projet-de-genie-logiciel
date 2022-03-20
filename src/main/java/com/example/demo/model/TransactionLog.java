package com.example.demo.model;


import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CompositePK.TransactionLogPK;
import com.example.demo.request.TransactionReq;
import lombok.*;
import org.hibernate.engine.internal.Cascade;

import javax.persistence.*;
import java.sql.Date;

import static javax.persistence.GenerationType.SEQUENCE;

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
            name = "direction",
            nullable = false
    )
    private Integer direction;

    @ManyToOne
    @JoinColumn(
            name="transaction_type_id",
            referencedColumnName = "transaction_type_id"
    )
    private TransactionType transactionTypeId;

    @Column(name="transaction_date")
    private Date transaction_date;

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

    public TransactionLog(TransactionReq transactionReq) {
        transaction_date = new Date(System.currentTimeMillis());
        transactionAmount = transactionReq.getTransactionAmount();
    }
}
