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

    //TODO : Check if the CascadeType ALL is good in all the cases
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="swift",
            referencedColumnName = "swift"
    )
    private Bank swift;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name= "user_id",
            referencedColumnName = "nrn"
    )
    private User userId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="account_type_id",
            referencedColumnName = "account_type_id",
            nullable = false
    )
    private AccountType accountTypeId;

    @Column(nullable = false)
    private Boolean payment;
}
