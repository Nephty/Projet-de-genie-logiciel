package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BankReq {

    private String swift;

    private String name;

    private String login;

    private String password;

    private String address;

    private String country;

    private Integer defaultCurrencyType;

    public boolean isInvalid() {
        return false;
    }

}
