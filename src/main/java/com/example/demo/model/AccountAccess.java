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
    @ManyToOne
    @JoinColumn(
            name="account_id",
            referencedColumnName = "iban"
    )
    private Account accountId;

    @Id
    @ManyToOne
    @JoinColumn(
            name="user_id",
            referencedColumnName = "nrn"
    )
    private User userId;

    @Column
    private boolean access;
    @Column
    private boolean hidden;
}
