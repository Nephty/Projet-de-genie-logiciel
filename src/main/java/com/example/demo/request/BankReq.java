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

    /**
     * Check if the request body is ok for posting.
     *
     * <br>To post a bank we need at least :
     * <ul>
     *     <li>swift</li>
     *     <li>name</li>
     *     <li>address</li>
     *     <li>country</li>
     *     <li>defaultCurrencyId</li>
     * </ul>
     * @return true if the request body is valid for posting a bank
     */
    @JsonIgnore
    public boolean isPostValid() {
        return swift != null
                && name != null
                && password != null
                && address != null
                && country != null
                && defaultCurrencyId != null;
    }

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br>To modify a bank we need at least :
     * <ul>
     *     <li>password</li>
     * </ul>
     * @return true if the request body is valid for modifying a bank.
     */
    @JsonIgnore
    public boolean isPutValid() {
        return password != null;
    }

    /**
     * Creates a request body for a bank with a {@link Bank} object.
     * @param bank The bank we want to get the request body.
     * @param censored boolean value to know if the password will be censored or not.
     */
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
