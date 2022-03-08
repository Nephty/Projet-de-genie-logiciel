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

    @ManyToOne
    @JoinColumn(
            name="swift",
            referencedColumnName = "swift"
    )
    private Bank swift;

    @ManyToOne
    @JoinColumn(
            name= "user_id",
            referencedColumnName = "nrn"
    )
    private User userId;

    @ManyToOne
    @JoinColumn(
            name="account_type_id",
            referencedColumnName = "account_type_id"
    )
    private AccountType accountTypeId;

    @Column
    private boolean payment;
}
