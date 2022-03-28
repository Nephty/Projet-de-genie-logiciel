package com.example.demo.request;

import com.example.demo.model.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AccountReq {

    private String iban;

    private String swift;

    private String userId;

    private Integer accountTypeId;

    private Boolean payment;

    //Response only

    private String ownerName;

    private BankReq linkedBank;

    @JsonIgnore
    public boolean isPostValid() {
        return iban != null
                && swift != null
                && userId != null
                && accountTypeId != null
                && payment != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return accountTypeId != null || payment != null;
    }

    public AccountReq(Account account) {
        iban = account.getIban();
        swift = account.getSwift().getSwift();
        userId = account.getUserId().getUserID();
        ownerName = account.getUserId().getFullName();
        accountTypeId = account.getAccountTypeId().getAccountTypeId();
        payment = account.getPayment();
        linkedBank = new BankReq(account.getSwift());
        linkedBank.setPassword(null);
    }
}
