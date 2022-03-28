package com.example.demo.controller;


import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.SubAccount;
import com.example.demo.request.SubAccountReq;
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
     * @param iban iban of the sub account to retrieve
     * @param currencyId currency of the sub account desired
     * @return the sub account matching the params provided
     * 200 - OK
     * 404 - Not found
     */
    @GetMapping
    public ResponseEntity<SubAccountReq> getSubAccount(@RequestParam String iban, @RequestParam Integer currencyId){
        SubAccountReq subAccountReq = subAccountService.getSubAccount(iban,currencyId);
        return new ResponseEntity<>(subAccountReq,HttpStatus.OK);
    }

    /**
     * @param subAccountReq [body] sub account to be added to the DB
     * @return sub account to String
     * 201 - Created
     * 400 - Bad Format
     * 409 - Bad FK
     * Who ? owner of the account
     */
    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody SubAccountReq subAccountReq) {
        if(!subAccountReq.isPostValid()) throw new MissingParamException();

        SubAccount savedSubAccount = subAccountService.addSubAccount(subAccountReq);
        return new ResponseEntity<>(savedSubAccount.toString(), HttpStatus.CREATED);
    }
    /**
     * @param iban [param] id of the account linked
     * @param currencyId [param] currency of this sub account
     * @return params sent
     * 200 - OK
     * Who ? owner of the sub account and maybe the bank
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
     * Who ? owner of the sub account
     * What ? /
     */
    @PutMapping
    public ResponseEntity<String> changeSubAccount(@RequestBody SubAccountReq subAccountReq) {
        if(!subAccountReq.isPutValid()) throw new MissingParamException();

        SubAccount savedSubAccount = subAccountService.changeSubAccount(subAccountReq);
        return new ResponseEntity<>(savedSubAccount.toString(), HttpStatus.CREATED);
    }
}
