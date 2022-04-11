package com.example.demo.model;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.request.SubAccountReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


/**
 * The model for the table sub_account.
 * Each account has at least a subAccount .
 * (in this application, only one because the application handles only one currency) <br>
 * Check the entity relationShip diagram in the documentation if you need more info about this table <br>
 * Setters, Getters, NoArgsConstructor, AllArgsConstructor and ToString method are implemented by {@link lombok}
 * @see Account
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sub_account")
public class SubAccount {

    @EmbeddedId
    private SubAccountPK subAccountPK;

    @JsonIgnore
    @ManyToOne
    @MapsId("iban")
    @JoinColumn(
            name="iban",
            referencedColumnName = "iban"
    )
    private Account iban;

    @JsonIgnore
    @ManyToOne
    @MapsId("currencyTypeId")
    @JoinColumn(
            name="currency_type_id",
            referencedColumnName = "currency_type_id"
    )
    private CurrencyType currencyType;

    @Column(
            name="current_balance",
            nullable = false
    )
    private Double currentBalance;

    public SubAccount(SubAccountPK subAccountPK) {
        this.subAccountPK = subAccountPK;
    }

    public SubAccount(Account iban, CurrencyType currencyType, Double currentBalance) {
        this.iban = iban;
        this.currencyType = currencyType;
        this.currentBalance = currentBalance;

        this.subAccountPK = new SubAccountPK(iban.getIban(),currencyType.getCurrencyId());
    }

    public SubAccount(SubAccountReq subAccountReq) {
        subAccountPK = new SubAccountPK(subAccountReq.getIban(), subAccountReq.getCurrencyType());
        currentBalance = subAccountReq.getCurrentBalance();
        if(currentBalance == null) {
            currentBalance = 0.0;
        }
    }

    public void change(SubAccountReq subAccountReq) {
        //TODO this
    }

    public static SubAccount createDefault(Account account) {
        SubAccount defaultSubAccount = new SubAccount();
        defaultSubAccount.setSubAccountPK(
                new SubAccountPK(
                        account.getIban(),
                        account.getSwift().getDefaultCurrencyType().getCurrencyId())
        );
        defaultSubAccount.setCurrentBalance(0.0);
        defaultSubAccount.setIban(account);
        defaultSubAccount.setCurrencyType(account.getSwift().getDefaultCurrencyType());
        return defaultSubAccount;
    }
}
