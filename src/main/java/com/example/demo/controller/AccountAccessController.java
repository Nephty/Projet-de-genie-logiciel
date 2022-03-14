package com.example.demo.controller;


import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account-access")
@RestController
public class AccountAccessController {

    private final AccountAccessService accountAccessService;
    //TODO To add in the get account maybe [URGENT]
    /**
     * Send a list of rights that the user has on the wallet he has access to
     * @param userId [path] id of the user
     * @return List of Account access
     * 200 - OK
     *
     */
    @GetMapping(value = "{userId}")
    public ResponseEntity<List<AccountAccess>> sendAccountAccess(@PathVariable String userId){
        return new ResponseEntity<>(accountAccessService.getAccountAccessByUserId(userId), HttpStatus.OK);
    }

    /**
     * @param accountAccess [body] Account access to add to the DB
     * @return account access to String
     * 201 - Created
     * 400 - Bad format
     */
    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
        return new ResponseEntity<>(accountAccess.toString(), HttpStatus.CREATED);
    }

    /**
     * @param accountAccess [body] Account access to change in the DB
     * @return account access to string
     * 201 - SUCCESS
     * 400 - Bad format
     */
    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
        return new ResponseEntity<>(accountAccess.toString(), HttpStatus.CREATED);
    }

    /**
     * @param accountId id of the account access
     * @param userId id of the account access
     * @return params sent
     * 200 - OK
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId + " : " + userId, HttpStatus.CREATED);
    }
}
