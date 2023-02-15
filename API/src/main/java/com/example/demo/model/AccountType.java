package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The model for the table account_type.
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 * @see Account
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="account_type")
public class AccountType {
    @Column(name= "account_type_id") @Id
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

    @Column(
            name="annual_fee",
            nullable = false
    )
    private Double annualFee;

    @Column(name="age_restriction")
    private Integer ageRestriction;
    @Column(name="transaction_amount_limit")
    private Integer transactionAmountLimit;

}
