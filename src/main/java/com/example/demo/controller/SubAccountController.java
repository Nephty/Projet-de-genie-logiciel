package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.SubAccount;
import com.example.demo.service.SubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(value = "/api/account/sub-account")
@RestController
public class SubAccountController {

    private final SubAccountService subAccountService;

    /**
     * @param subAccount [body] SubAccount to be added to the DB
     * @return SubAccount to String in the body
     * 201 - Created
     * 400 - Bad Format
     */
    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.addSubAccount(subAccount);
        return new ResponseEntity<>(subAccount.toString(), HttpStatus.CREATED);
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
