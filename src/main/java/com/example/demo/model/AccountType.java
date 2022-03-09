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
@Table(name="account_type")
public class AccountType {
    @Column(name="Account_type_id") @Id
    private Integer accountTypeId;

    @Column(
            name="account_type_name",
            nullable = false
    )
    private String accountTypeName;

    @Column(
            name="annual_return",
            nullable = false
    )
    private Double annualReturn;
    @Column(name="age_restriction")
    private Integer ageRestriction;
    @Column(name="transaction_amount_limit")
    private Integer transactionAmountLimit;

}
