package com.example.demo.model;

import com.example.demo.request.AccountReq;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;


/**
 * The model for the table accounts.
 * Each account has one {@link SubAccount} <br>
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
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

    /**
     * Custom constructor for Account with the custom Request.<br>
     * Set the next process date automatically for next year, the payment to false.
     * @param accountReq Custom request for creating an account. Only the iban is used in this case.
     */
    public Account(AccountReq accountReq) {
        this.iban = accountReq.getIban();
        this.payment = false; // It's the default mode for accounts according to the instructions
        nextProcess = new Date(
                new Date(System.currentTimeMillis()).toLocalDate().plusYears(1).toEpochDay()
        );
    }

    /**
     * Modify the Payment of the Account (could also modify the iban)
     * @param accountReq Custom request for changing the account (only the Payment is required)
     */
    public void change(AccountReq accountReq) {
        if(accountReq.getIban() != null) {
            iban = accountReq.getIban();
        }
        if(accountReq.getPayment() != null) {
            payment = accountReq.getPayment();
        }
    }
}
