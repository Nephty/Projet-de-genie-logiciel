package com.example.demo.request;

import com.example.demo.model.AccountAccess;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
public class AccountAccessReq {

    private String accountId;

    private String userId;

    private Boolean access;

    private Boolean hidden;

    public AccountAccessReq(
            String accountId,
            String userId,
            Boolean access,
            Boolean hidden
    ) {
        this.accountId = accountId;
        this.userId = userId;
        this.access = access;
        this.hidden = hidden;
    }

    //Response
    private AccountReq account;

    //TODO assert payer has access and account is active
    @JsonIgnore
    public boolean isPostValid() {
        return accountId != null
                && userId != null
                && access != null
                && hidden != null;
    }

    @JsonIgnore
    public boolean isPutValid() {
        return access != null || hidden != null;
    }

    public AccountAccessReq(AccountAccess accountAccess) {
        accountId = accountAccess.getAccountId().getIban();
        account = new AccountReq(accountAccess.getAccountId());
        userId = accountAccess.getUserId().getUserId();
        access = accountAccess.getAccess();
        hidden = accountAccess.getHidden();
    }
}
