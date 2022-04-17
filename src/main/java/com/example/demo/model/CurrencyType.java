package com.example.demo.model;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The model for the table currency_type.
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
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
    private Integer currencyId;

    @Column(
            nullable = false,
            unique = true,
            name = "currency_type_name"
    )
    private String currencyTypeName;
}
