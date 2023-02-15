package com.example.demo.model;


import com.example.demo.request.BankReq;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;


/**
 * The model for the table banks.
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "banks")
public class Bank {
    @Column @Id
    private String swift;

    @Column(
            nullable = false
    )
    private String name;

    @Column(
            nullable = false
    )
    private String password;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String country;

    @ManyToOne
    @JoinColumn(
            name="default_currency_type",
            referencedColumnName = "currency_type_id",
            nullable = false
    )
    private CurrencyType defaultCurrencyType;


    /**
     * Custom constructor for Bank with the request structure. <br>
     * @param bankReq Custom request for creating a Bank.
     */
    public Bank(BankReq bankReq) {
        swift = bankReq.getSwift();
        name = bankReq.getName();
        password = bankReq.getPassword();
        address = bankReq.getAddress();
        country = bankReq.getCountry();
    }

    /**
     * Modify the password of the account
     * @param bankReq Custom request for the account (only the password is used in this case)
     */
    public void change(BankReq bankReq, PasswordEncoder passwordEncoder) {
        if(bankReq.getPassword() != null) {
            password = passwordEncoder.encode(bankReq.getPassword());
        }
    }

}
