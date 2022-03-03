package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "banks")
@Table(name = "banks")
public class Bank {
    @Column @Id
    private String swift;
    @Column
    private String name;
    @Column
    private String login;
    @Column
    private String password;
    @Column
    private String address;
    @Column
    private String country;

    @ManyToOne
    @JoinColumn(name= "default_currency_type", foreignKey = @ForeignKey(name = "fk_currencyType"))
    private CurrencyType default_currencyType;
}
