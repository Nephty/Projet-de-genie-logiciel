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
}
