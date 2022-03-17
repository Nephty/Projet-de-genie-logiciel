package com.example.demo.model;

import com.example.demo.request.AccountReq;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Column
    @Id
    private String iban;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="swift",
            referencedColumnName = "swift"
    )
    private Bank swift;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name= "user_id",
            referencedColumnName = "nrn"
    )
    private User userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="account_type_id",
            referencedColumnName = "account_type_id",
            nullable = false
    )
    private AccountType accountTypeId;

    @Column(nullable = false)
    private Boolean payment;

    public Account(String iban, Bank swift) {
        this.iban = iban;
        this.swift = swift;
    }

    public Account(AccountReq accountReq) {
        iban = accountReq.getIban();
        payment = accountReq.getPayment();
    }
}
