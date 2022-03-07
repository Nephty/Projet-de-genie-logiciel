package com.example.demo.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="account_access")
public class AccountAccess {
    @Id
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "user_id")
    private String userId;
    @Column
    private boolean access;
    @Column
    private boolean hidden;
}
