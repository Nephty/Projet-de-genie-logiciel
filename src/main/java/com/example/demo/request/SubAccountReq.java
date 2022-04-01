package com.example.demo.request;

import com.example.demo.model.SubAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class SubAccountReq {

    private String iban;

    private Integer currencyType;

    private Double currentBalance;

    //Response only

    private String currencyTypeName;

    @JsonIgnore
    public boolean isPostValid() {
        return iban != null
                && currencyType != null
                && currentBalance != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return currentBalance != null || currencyType != null;
    }

    public SubAccountReq(SubAccount subAccount) {
        iban = subAccount.getIban().getIban();
        currencyType = subAccount.getCurrencyType().getCurrencyId();
        currentBalance = subAccount.getCurrentBalance();
        currencyTypeName = subAccount.getCurrencyType().getCurrency_type_name();
    }
}
