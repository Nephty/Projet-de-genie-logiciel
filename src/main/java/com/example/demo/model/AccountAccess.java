package com.example.demo.model;

import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.request.AccountAccessReq;
import lombok.*;

import javax.persistence.*;

/**
 * The model for the table account_access.
 * This class as a composite primary key designed with an idClass {@link AccountAccessPK} <br>
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 */
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

    /**
     * Custom constructor for AccountAccess with the custom Request. <br>
     * Set the access and the hidden attributes.
     * @param accountAccessReq Custom request for creating/modifying an AccountAccess
     */
    public AccountAccess(AccountAccessReq accountAccessReq) {
        access = accountAccessReq.getAccess();
        hidden = accountAccessReq.getHidden();
    }

    /**
     * Modify the Access and/or the hidden attributes if they are present in the Request.
     * @param accountAccessReq Custom request for creating/modifying an AccountAccess
     */
    public void change(AccountAccessReq accountAccessReq) {
        if(accountAccessReq.getAccess() != null) {
            access = accountAccessReq.getAccess();
        }
        if(accountAccessReq.getHidden() != null) {
            hidden = accountAccessReq.getHidden();
        }
    }

    /**
     * Creates a default access for the owner of the account.
     * @param account The account that we just created.
     * @return The created access
     */
    public static AccountAccess createDefault(Account account) {
        AccountAccess defaultAccountAccess = new AccountAccess();
        defaultAccountAccess.setUserId(account.getUserId());
        defaultAccountAccess.setAccess(true);
        defaultAccountAccess.setHidden(false);
        defaultAccountAccess.setAccountId(account);

        return defaultAccountAccess;
    }
}
