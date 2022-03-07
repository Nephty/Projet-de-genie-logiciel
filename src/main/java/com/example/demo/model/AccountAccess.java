package com.example.demo.model;

import com.example.demo.model.CompositePK.AccountAccessPK;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@IdClass(AccountAccessPK.class)
@Table(name="account_access")
public class AccountAccess {
    @Id
    @Column(name = "account_id")
    private String accountId;
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column
    private boolean access;
    @Column
    private boolean hidden;
}
