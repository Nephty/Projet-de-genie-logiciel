package com.example.demo.model;


import com.example.demo.request.BankReq;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

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
            nullable = false,
            unique = true
    )
    private String login;

    @Column(
            nullable = false
    )
    private String password;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String country;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="default_currency_type",
            referencedColumnName = "currency_type_id",
            nullable = false
    )
    private CurrencyType defaultCurrencyType;
  


    public Bank(BankReq bankReq) {
        swift = bankReq.getSwift();
        name = bankReq.getName();
        login = bankReq.getLogin();
        password = bankReq.getPassword();
        address = bankReq.getAddress();
        country = bankReq.getCountry();
    }

}
