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
@Entity(name="currency_type")
@Table(name = "currency_type")
public class CurrencyType {

    @Column(name = "currency_type_id")
    @Id
    private int currencyId;

    @Column
    private String currency_type_name;
}
