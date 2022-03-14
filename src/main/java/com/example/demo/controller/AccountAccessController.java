package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public void addAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
    }
    @PutMapping
    public void changeAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
    }
    @DeleteMapping
    public void deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
    }
}
