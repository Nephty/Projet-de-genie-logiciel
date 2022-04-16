package com.example.demo.controller;


import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account-access")
@RestController @Slf4j
public class AccountAccessController {

    private final AccountAccessService accountAccessService;

    /**
     * Returns a list with all the account access for a certain user
     * @param userId id of the user
     * @return Array of account access
     * 200 - OK
     * 204 - Not found
     * Who ? the user
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<AccountAccessReq>> sendAccountAccess(
            @RequestParam String userId,
            @RequestParam Boolean deleted, @RequestParam Boolean hidden){
        if (deleted){
            // If we want hidden and deleted, it only returns the deleted.
            return new ResponseEntity<>(
                    accountAccessService.getAccessToDeletedAccount(userId),
                    HttpStatus.OK
            );
        }
        if (hidden){
            return new ResponseEntity<>(
                    accountAccessService.getAccessToHiddenAccount(userId),
                    HttpStatus.OK
            );
        }
        // If hidden and deleted is false, return all access (used for portfolio)
        return new ResponseEntity<>(
                accountAccessService.getAccountAccessByUserId(userId),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/all/co-owner")
    public ResponseEntity<List<User>> getAllOwners(@RequestParam String iban){
        return new ResponseEntity<>(
                accountAccessService.findAllOwners(iban),
                HttpStatus.OK
        );
    }


    /**
     * @param userId id of the user with the access
     * @param accountId id of the user account
     * @return account access matching params
     * 200 - OK
     * 404 - Not Found
     * Who ? the owner of the account and the bank
     */
    @GetMapping(value = "{userId}/{accountId}")
    public ResponseEntity<AccountAccessReq> sendAccountAccess(@PathVariable String userId, @PathVariable String accountId){
        return new ResponseEntity<>(
                accountAccessService.findAccountAccess(accountId, userId),
                HttpStatus.OK
        );
    }

    /**
     * @param accountAccessReq [body] account access to be added to the DB
     * @return account access to String
     * 201 - Created
     * 400 - Bad Format
     * 409 - Conflict
     * Who ? the owner of the account and/or the bank
     */
    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody AccountAccessReq accountAccessReq){
        log.info("inserting account-access: {}", accountAccessReq);
        if(!accountAccessReq.isPostValid()) throw new MissingParamException();

        // TODO: 4/14/22 Empêcher une bank extérieure de créer un accès vers un compte. 
        AccountAccess savedAccountAccess = accountAccessService.createAccountAccess(accountAccessReq);
        return new ResponseEntity<>(savedAccountAccess.toString(), HttpStatus.CREATED);
    }


    /**
     * @param accountAccessReq [body] account access to be changed in the DB
     * @return account access to String
     * 201 - Created
     * 400 - Bad Format
     * 404 - Not found
     * Who ? the owner of the account and/or the bank
     */
    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccessReq accountAccessReq) {
        if(!accountAccessReq.isPutValid()) throw new MissingParamException();

        AccountAccess savedAccountAccess = accountAccessService.changeAccountAccess(accountAccessReq);
        return new ResponseEntity<>(savedAccountAccess.toString(), HttpStatus.CREATED);
    }

    /**
     * @param userId id of the user with the access
     * @param accountId id of the user account
     * @return params sent
     * 200 - OK
     * Who ? the owner of the account and/or the bank
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId + " : " + userId, HttpStatus.OK);
    }
}
