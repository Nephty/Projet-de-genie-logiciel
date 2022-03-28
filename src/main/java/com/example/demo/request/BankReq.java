package com.example.demo.request;

import com.example.demo.model.Bank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BankReq {

    private String swift;

    private String name;

    private String password;

    private String address;

    private String country;

    private Integer defaultCurrencyType;
    @JsonIgnore
    public boolean isPostValid() {
        return swift != null
                && name != null
                && password != null
                && address != null
                && country != null
                && defaultCurrencyType != null;
    }
    @JsonIgnore
    public boolean isPutValid() {
        return password != null;
    }

    public BankReq(Bank bank) {
        swift = bank.getSwift();
        name = bank.getName();
        password = bank.getPassword();
        address = bank.getAddress();
        country = bank.getCountry();
        defaultCurrencyType = bank.getDefaultCurrencyType().getCurrencyId();
    }
}
