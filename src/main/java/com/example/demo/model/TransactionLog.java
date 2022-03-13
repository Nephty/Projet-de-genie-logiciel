package com.example.demo.model;


import com.example.demo.model.CompositePK.SubAccountPK;
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
@Entity
@Table(name="transaction_log")
public class TransactionLog {

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

    @ManyToOne(cascade = CascadeType.ALL)
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
            name = "direction",
            nullable = false
    )
    private Integer direction;

}
