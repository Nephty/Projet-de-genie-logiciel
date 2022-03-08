package com.example.demo.model;

import lombok.*;

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
    private String swift;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name= "user_id")
    private String userId;

    @ManyToOne(targetEntity = AccountType.class)
    @JoinColumn(name="account_type_id")
    private Integer accountTypeId;

    @Column
    private boolean payment;
}
