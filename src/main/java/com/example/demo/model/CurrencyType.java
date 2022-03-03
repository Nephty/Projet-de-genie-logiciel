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
    @Column @Id
    private int currency_type_id;

    @Column
    private String currency_type_name;
}
