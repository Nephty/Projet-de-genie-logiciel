package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transaction_type")
public class TransactionType {

    @Column(name="transaction_type_id")
    @Id
    private Integer transactionTypeId;

    @Column(
            name="transaction_type_name",
            nullable = false,
            unique = true
    )
    private String transactionTypeName;

    @Column(name="transaction_fee")
    private Double transactionFee;
}
