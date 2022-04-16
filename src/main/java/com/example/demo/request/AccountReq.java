package com.example.demo.request;

import com.example.demo.model.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@Data @AllArgsConstructor
public class AccountReq {

    private String iban;

    private String swift;

    private String userId;

    private Integer accountTypeId;

    private Boolean payment;

    //Response only

    private String ownerFirstname;

    private String ownerLastname;

    private BankReq linkedBank;

    private Date nextProcess;

    private Boolean deleted;

    @JsonIgnore
    public boolean isPostValid() {
        return iban != null
                && swift != null
                && userId != null
                && accountTypeId != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return iban != null && (accountTypeId != null || payment != null);
    }

    public AccountReq(Account account) {
        iban = account.getIban();
        swift = account.getSwift().getSwift();
        userId = account.getUserId().getUserId();
        ownerFirstname = account.getUserId().getFirstname();
        ownerLastname = account.getUserId().getLastname();
        accountTypeId = account.getAccountTypeId().getAccountTypeId();
        payment = account.getPayment();
        deleted = account.getDeleted();
        linkedBank = new BankReq(account.getSwift(), true);
        nextProcess = account.getNextProcess();
    }
}
