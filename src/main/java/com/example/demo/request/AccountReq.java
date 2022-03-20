package com.example.demo.request;

import com.example.demo.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccountReq {

    private String iban;

    private String swift;

    private String userId;

    private Integer accountTypeId;

    private Boolean payment;

    public AccountReq(Account account) {
        iban = account.getIban();
        swift = account.getSwift().getSwift();
        userId = account.getUserId().getUserID();
        accountTypeId = account.getAccountTypeId().getAccountTypeId();
        payment = account.getPayment();
    }
}
