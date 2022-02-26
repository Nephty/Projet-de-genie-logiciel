package com.example.demo.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accounts")
//@Table(name = "accounts")
public class Account {
    @Column
    @Id
    private String iban;
    @Column
    private String SWIFT;
    @Column
    private String userId;
    @Column
    private int accountTypeId;
    @Column
    private boolean payment;
}
