package com.example.demo.model;

import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.request.AccountAccessReq;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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
@ManyToOne
    @JoinColumn(
            name="account_id",
            referencedColumnName = "iban",
            nullable = false
    )
    private Account accountId;

    @Id
    @ManyToOne
    @JoinColumn(
            name="user_id",
            referencedColumnName = "nrn",
            nullable = false
    )
    private User userId;

    @Column
    private Boolean access;
    @Column
    private Boolean hidden;

    public AccountAccess(AccountAccessReq accountAccessReq) {
        access = accountAccessReq.getAccess();
        hidden = accountAccessReq.getHidden();
    }

    public void change(AccountAccessReq accountAccessReq) {
        if(accountAccessReq.getAccess() != null) {
            access = accountAccessReq.getAccess();
        }
        if(accountAccessReq.getHidden() != null) {
            hidden = accountAccessReq.getHidden();
        }
    }
}
