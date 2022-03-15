package com.example.demo.controller;


import com.example.demo.request.SubAccountReq;
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
    //TODO return a list of sub account linked to an account -> send along with the account
    /**
     * @param subAccountReq [body] sub account to be added to the DB
     * @return sub account to String
     * 201 - Created
     * 400 - Bad Format
     * 409 - Bad FK
     */
    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody SubAccountReq subAccountReq) {
        subAccountService.addSubAccount(subAccountReq);
        return new ResponseEntity<>(subAccountReq.toString(), HttpStatus.CREATED);
    }

    /**
     * @param iban [param] id of the account linked
     * @param currencyId [param] currency of this sub account
     * @return params sent
     * 200 - OK
     */
    @DeleteMapping
    public ResponseEntity<String> deleteSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        subAccountService.deleteSubAccount(iban, currencyId);
        return new ResponseEntity<>(iban + " : " + currencyId, HttpStatus.OK);
    }

    /**
     * @param subAccountReq [body] sub account to be changed in the DB
     * @return sub account to String
     * 201 - Created
     * 400 - Bad Format
     * 409 - Bad FK
     */
    @PutMapping
    public ResponseEntity<String> changeSubAccount(@RequestBody SubAccountReq subAccountReq) {
        subAccountService.changeSubAccount(subAccountReq);
        return new ResponseEntity<>(subAccountReq.toString(), HttpStatus.CREATED);
    }
}
