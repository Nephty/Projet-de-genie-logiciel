package com.example.demo.model;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.request.SubAccountReq;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

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
    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("iban")
    @JoinColumn(
            name="iban",
            referencedColumnName = "iban"
    )
    private Account iban;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
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

    public SubAccount(SubAccountReq subAccountReq) {
        subAccountPK = new SubAccountPK(subAccountReq.getIban(), subAccountReq.getCurrencyType());
        currentBalance = subAccountReq.getCurrentBalance();
        if(currentBalance == null) {
            currentBalance = 0.0;
        }
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
