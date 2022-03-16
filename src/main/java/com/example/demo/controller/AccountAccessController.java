package com.example.demo.controller;


import com.example.demo.model.AccountAccess;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account-access")
@RestController
public class AccountAccessController {

    private final AccountAccessService accountAccessService;

    /**
     * Returns a list with all the account access for a certain user
     * @param userId [path] id of the user
     * @return Array of account access
     * 200 - OK
     */
    @GetMapping(value = "{userId}")
    public ResponseEntity<List<AccountAccess>> sendAccountAccess(@PathVariable String userId){
        return new ResponseEntity<>(
                accountAccessService.getAccountAccessByUserId(userId),
                HttpStatus.OK
        );
    }

    /**
     * @param userId id of the user with the access
     * @param accountId id of the user account
     * @return account access matching params
     * 200 - OK
     * 404 - Not Found
     */
    @GetMapping(value = "{userId}/{accountId}")
    public ResponseEntity<AccountAccess> sendAccountAccess(@PathVariable String userId, @PathVariable String accountId){
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
     */
    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody AccountAccessReq accountAccessReq){
        AccountAccess res = accountAccessService.createAccountAccess(accountAccessReq);
        return new ResponseEntity<>(res.toString(), HttpStatus.CREATED);
    }


    /**
     * @param accountAccessReq [body] account access to be changed in the DB
     * @return account access to String
     * 201 - Created
     * 400 - Bad Format
     * 409 - Conflict
     */
    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccessReq accountAccessReq) {
        AccountAccess res = accountAccessService.changeAccountAccess(accountAccessReq);
        return new ResponseEntity<>(res.toString(), HttpStatus.CREATED);
    }

    /**
     * @param userId id of the user with the access
     * @param accountId id of the user account
     * @return params sent
     * 200 - OK
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId + " : " + userId, HttpStatus.OK);
    }
}