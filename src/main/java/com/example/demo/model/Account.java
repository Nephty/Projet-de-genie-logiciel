package com.example.demo.model;

import lombok.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accounts")
@Table(name = "accounts")
public class Account {
    @Column
    @Id
    private String iban;
    @Column
    private String swift;

    @ManyToOne
    private User user;

    @Column(name = "account_type_id")
    private int accountTypeId;
    @Column
    private boolean payment;
}
