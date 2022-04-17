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

    /**
     * Constructor for the SubAccount without the SubAccount primaryKey taken in the {@link lombok} constructor.
     * @param iban The iban of the account
     * @param currencyType The currency of the account
     * @param currentBalance The balance of the account
     */
    public SubAccount(Account iban, CurrencyType currencyType, Double currentBalance) {
        this.iban = iban;
        this.currencyType = currencyType;
        this.currentBalance = currentBalance;

        this.subAccountPK = new SubAccountPK(iban.getIban(),currencyType.getCurrencyId());
    }

    /**
     * Custom constructor for SubAccount with the custom body.
     * If the currentBalance of the body is null, with set it to 0.
     * @param subAccountReq Custom req body for creating/modifying a SubAccount
     */
    public SubAccount(SubAccountReq subAccountReq) {
        subAccountPK = new SubAccountPK(subAccountReq.getIban(), subAccountReq.getCurrencyType());
        currentBalance = subAccountReq.getCurrentBalance();
        if(currentBalance == null) {
            currentBalance = 0.0;
        }
    }

    /**
     * Modify the balance of the SubAccount.
     * @param subAccountReq Custom req body for creating/modifying a SubAccount (only currentBalance used)
     */
    public void change(SubAccountReq subAccountReq) {
        currentBalance = subAccountReq.getCurrentBalance();
    }

    /**
     * Creates a default subAccount for the account. (each account has at least one subAccount)
     * @param account The account that needs a default subAccount.
     * @return the SubAccount created.
     */
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
