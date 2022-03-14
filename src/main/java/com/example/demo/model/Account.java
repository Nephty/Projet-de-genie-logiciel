package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;


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

    public String toString(){
        String res = "Account(";
        if (iban != null)
            res += "iban="+iban;
        if (swift != null)
            res += "swift="+swift;
        if (userId != null)
            res += "userId"+userId;
        if (accountTypeId != null)
            res += "accountTypeId="+accountTypeId;
        if (payment != null)
            res += "payment="+payment;
        res+=")";
        return res;
    }
}
