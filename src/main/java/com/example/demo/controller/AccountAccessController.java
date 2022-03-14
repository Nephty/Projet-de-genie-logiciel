package com.example.demo.controller;


import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.service.AccountAccessService;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
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
    private final UserService userService;
    private final AccountService accountService;

    @GetMapping(value = "{userId}")
    public List<AccountAccess> sendAccountAccess(@PathVariable String userId){
        return accountAccessService.getAccountAccessByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody Map<String,String> json){
        //parsing the body
        boolean access = Boolean.parseBoolean(json.get("access"));
        boolean hidden = Boolean.parseBoolean(json.get("hidden"));

        //Throws a EntityNotFound Exception if the User/Account doesn't exist.
        User user = userService.getUserById(json.get("userID"));
        Account account = accountService.getAccount(json.get("iban"));

        //Create the accountAccess object that we want to save
        AccountAccess res = new AccountAccess(
                account,
                user,
                access,
                hidden
        );
        accountAccessService.createAccountAccess(res);
        return new ResponseEntity<>(res.toString(), HttpStatus.CREATED);
    }


    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.changeAccountAccess(accountAccess);
        return new ResponseEntity<>(accountAccess.toString(), HttpStatus.CREATED);
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId, HttpStatus.CREATED);
    }
}
