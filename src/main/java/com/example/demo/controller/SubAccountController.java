package com.example.demo.controller;

import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.service.SubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping(value = "/api/account/sub-account")
@RestController
public class SubAccountController {

    private final SubAccountService subAccountService;

    @GetMapping
    public ResponseEntity<SubAccount> getSubAccount(@RequestParam String iban, @RequestParam Integer currencyId){
        SubAccount subAccount = subAccountService.getSubAccount(iban,currencyId);
        return new ResponseEntity<>(subAccount,HttpStatus.OK);
    }

    /**
     * @return SubAccount to String in the body
     * 201 - Created
     * 400 - Bad Format
     */
    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody Map<String, String> json) {
        //TODO doesn't work, need rework
        Double balance = 0.0;
        String balanceTmp = json.get("balance");
        if (balanceTmp != null)
            balance = Double.parseDouble(balanceTmp);
        String iban = json.get("iban");
        Integer currency = Integer.parseInt(json.get("currency"));
        return new ResponseEntity<String>(json.get("iban")+" "+json.get("currency"),HttpStatus.CREATED);
    }

    /**
     * @param iban [param] id of the sub account to delete
     * @param currencyId [param] id of the sub-account to delete
     * @return params sent
     * 200 - OK
     */
    @DeleteMapping
    public ResponseEntity<String> deleteSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        subAccountService.deleteSubAccount(iban, currencyId);
        return new ResponseEntity<>(iban + " : " + currencyId, HttpStatus.OK);
    }

    /**
     * @param subAccount [body] Sub account to change in the DB
     * @return Sub account to String
     * 201 - CREATED
     * 400 - Bad format
     */
    @PutMapping
    public ResponseEntity<String> changeSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.changeSubAccount(subAccount);
        return new ResponseEntity<>(subAccount.toString(), HttpStatus.CREATED);
    }
}
