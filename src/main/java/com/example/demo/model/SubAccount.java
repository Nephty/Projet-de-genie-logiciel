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
@Table(name="sub_account")
public class SubAccount {

    @Column @Id
    private String iban;
    @Column(name="currency_type_id")
    private int currencyTypeId;
    @Column(name="current_balance")
    private double currentBalance;

}
