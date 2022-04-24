package com.example.demo.request;

import com.example.demo.model.AccountAccess;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AccountAccessReq {

    private String accountId;

    private String userId;

    private Boolean access;

    private Boolean hidden;

    //Response
    private AccountReq account;

    /**
     * Default constructor for accountAccess request body
     * @param accountId The id of the account
     * @param userId The id of the user
     * @param access if the user has access to the account
     * @param hidden if the user want to hide the account in his portfolio.
     */
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

    /**
     * Check if the request body is ok for posting.
     *
     * <br>To post an account we need at least :
     * <ul>
     *     <li>accountId</li>
     *     <li>userId</li>
     *     <li>access</li>
     *     <li>hidden</li>
     * </ul>
     * @return true if the request body is valid for posting an access
     */
    @JsonIgnore
    public boolean isPostValid() {
        return accountId != null
                && userId != null
                && access != null
                && hidden != null;
    }

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br>To modify an access we need at least :
     * <ul>
     *     <li>access and/or hidden</li>
     * </ul>
     * @return true if the request body is valid for modifying access.
     */
    @JsonIgnore
    public boolean isPutValid() {
        return access != null || hidden != null;
    }

    /**
     * Creates a request body for access with an {@link AccountAccess} object.
     * @param accountAccess The accountAccess we want to get the request body.
     */
    public AccountAccessReq(AccountAccess accountAccess) {
        accountId = accountAccess.getAccountId().getIban();
        account = new AccountReq(accountAccess.getAccountId());
        userId = accountAccess.getUserId().getUserId();
        access = accountAccess.getAccess();
        hidden = accountAccess.getHidden();
    }
}
