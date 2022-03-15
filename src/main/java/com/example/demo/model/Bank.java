package com.example.demo.model;

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
    /*
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(
            name="default_currency_type",
            referencedColumnName = "currency_type_id",
            nullable = false
    )
    @JsonIgnore
    private CurrencyType defaultCurrencyType;*/
    /*
    @JsonAlias(value = "defaultCurrencyType")
    @Transient
    private Integer currency;*/

    @Column(name = "default_currency_type")
    private Integer defaultCurrencyType;

}
