package com.example.demo.model;

import com.example.demo.request.AccountReq;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.time.Duration;


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

    @ManyToOne
    @JoinColumn(
            name="swift",
            referencedColumnName = "swift"
    )
    private Bank swift;

    @ManyToOne
    @JoinColumn(
            name= "user_id",
            referencedColumnName = "nrn"
    )
    private User userId;

    @ManyToOne
    @JoinColumn(
            name="account_type_id",
            referencedColumnName = "account_type_id",
            nullable = false
    )
    private AccountType accountTypeId;

    @Column(nullable = false)
    private Boolean payment;

    @Column(
            nullable = false,
            name = "next_process"
    )
    private Date nextProcess;

    public Account(String iban, Bank swift) {
        this.iban = iban;
        this.swift = swift;
    }

    public Account(AccountReq accountReq) {
        this.iban = accountReq.getIban();
        this.payment = false; // It's the default mode for accounts according to the instructions
        nextProcess = new Date(
                new Date(System.currentTimeMillis()).toLocalDate().plusYears(1).toEpochDay()
        );
    }

    public void change(AccountReq accountReq) {
        if(accountReq.getIban() != null) {
            iban = accountReq.getIban();
        }
        if(accountReq.getPayment() != null) {
            payment = accountReq.getPayment();
        }
    }
}
