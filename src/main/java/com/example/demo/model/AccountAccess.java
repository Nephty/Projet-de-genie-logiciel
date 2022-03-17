package com.example.demo.model;

import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.request.AccountAccessReq;
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
@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(
            name="account_id",
            referencedColumnName = "iban",
            nullable = false
    )
    private Account accountId;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
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
}
