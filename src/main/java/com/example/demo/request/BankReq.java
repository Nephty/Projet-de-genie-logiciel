package com.example.demo.request;

import com.example.demo.model.Bank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankReq {

    private String swift;

    private String name;

    private String password;

    private String address;

    private String country;

    private Integer defaultCurrencyId;

    //Response only

    private String defaultCurrencyName;

    @JsonIgnore
    public boolean isPostValid() {
        return swift != null
                && name != null
                && password != null
                && address != null
                && country != null
                && defaultCurrencyId != null;
    }
    @JsonIgnore
    public boolean isPutValid() {
        return password != null;
    }

    public BankReq(Bank bank, boolean censored) {
        swift = bank.getSwift();
        name = bank.getName();
        password = censored ? null : bank.getPassword();
        address = bank.getAddress();
        country = bank.getCountry();
        defaultCurrencyId = bank.getDefaultCurrencyType().getCurrencyId();
        defaultCurrencyName = bank.getDefaultCurrencyType().getCurrencyTypeName();
    }
}
