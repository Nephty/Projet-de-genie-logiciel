package com.example.demo.controller;


import com.example.demo.model.AccountAccess;
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

    @GetMapping(value = "{userId}")
    public List<AccountAccess> sendAccountAccess(@PathVariable String userId){
        return accountAccessService.getAccountAccessByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody Map<String,String> json){
        //parsing the body
        boolean access = Boolean.parseBoolean(json.get("access"));
        boolean hidden = Boolean.parseBoolean(json.get("hidden"));
        String accountId = json.get("iban");
        String userId = json.get("userId");


        AccountAccess res = accountAccessService.createAccountAccess(accountId,userId,access,hidden);
        return new ResponseEntity<>(res.toString(), HttpStatus.CREATED);
    }


    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody Map<String,String> json) {
        String iban = json.get("iban");
        String userId = json.get("userId");
        boolean access = Boolean.parseBoolean(json.get("access"));
        boolean hidden = Boolean.parseBoolean(json.get("hidden"));
        AccountAccess res = accountAccessService.changeAccountAccess(iban,userId,access,hidden);
        return new ResponseEntity<>(res.toString(), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId, HttpStatus.CREATED);
    }
}
