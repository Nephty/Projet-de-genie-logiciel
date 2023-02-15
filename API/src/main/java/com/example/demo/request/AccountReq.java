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

    /**
     * Checks if the request body is ok for posting.
     *
     * <br> To post an account, we need at least :
     * <ul>
     *     <li>iban</li>
     *     <li>swift</li>
     *     <li>userID</li>
     *     <li>accountTypeId</li>
     * </ul>
     * @return True if the request body is valid for posting an account.
     */
    @JsonIgnore
    public boolean isPostValid() {
        return iban != null
                && swift != null
                && userId != null
                && accountTypeId != null;
    }

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br> To modify an account we need at least :
     * <ul>
     *     <li>iban</li>
     *     <li>accountTypeId and/or payment</li>
     * </ul>
     * @return true if the request body is valid for posting an account
     */
    @JsonIgnore
    public boolean isPutValid() {
        return iban != null && (accountTypeId != null || payment != null);
    }

    /**
     * Creates a request body for account with an {@link Account} object.
     * @param account The account we want to get the request body.
     */
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
