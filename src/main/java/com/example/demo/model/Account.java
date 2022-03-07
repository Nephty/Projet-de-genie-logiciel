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

    @ManyToOne(targetEntity = Bank.class)
    @JoinColumn(name="swift")
    private Bank swift;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="userID")
    private User userId;

    @ManyToOne(targetEntity = AccountType.class)
    @JoinColumn(name="account_type_id")
    private AccountType accountTypeId;

    @Column
    private boolean payment;
}
