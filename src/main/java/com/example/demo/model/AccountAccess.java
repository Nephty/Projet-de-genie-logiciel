package com.example.demo.model;

import com.example.demo.model.CompositePK.AccountAccessPK;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@IdClass(AccountAccessPK.class)
@Entity
@Table(name="account_access")
public class AccountAccess {
    @Id
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name="account_id")
    private Account accountId;

    @Id
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="user_id")
    private User userId;

    @Column
    private boolean access;
    @Column
    private boolean hidden;
}
