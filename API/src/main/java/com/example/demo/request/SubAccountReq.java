package com.example.demo.request;

import com.example.demo.model.SubAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubAccountReq {

    private String iban;

    private Integer currencyType;

    private Double currentBalance;

    //Response only

    private String currencyTypeName;

    /**
     * Check if the request body is ok for posting.
     *
     * <br>To post a SubAccount we need at least :
     * <ul>
     *     <li>iban</li>
     *     <li>currencyType</li>
     *     <li>currentBalance</li>
     * </ul>
     * @return true if the request body is valid for posting a SubAccount
     */
    @JsonIgnore
    public boolean isPostValid() {
        return iban != null
                && currencyType != null
                && currentBalance != null;
    }

    /**
     * Checks if the request body is ok for modifying.
     *
     * <br>To modify a SubAccount we need at least :
     * <ul>
     *     <li>iban</li>
     *     <li>currentBalance</li>
     * </ul>
     * @return true if the request body is valid for modifying SubAccount.
     */
    @JsonIgnore
    public boolean isPutValid() {
        return iban != null && currentBalance != null;
    }

    /**
     * Creates a request body for access with a {@link SubAccount } object.
     * @param subAccount The SubAccount we want to get the request body/
     */
    public SubAccountReq(SubAccount subAccount) {
        iban = subAccount.getIban().getIban();
        currencyType = subAccount.getCurrencyType().getCurrencyId();
        currentBalance = subAccount.getCurrentBalance();
        currencyTypeName = subAccount.getCurrencyType().getCurrencyTypeName();
    }
}
