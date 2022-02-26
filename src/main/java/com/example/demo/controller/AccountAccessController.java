package com.example.demo.controller;

import com.example.demo.model.AccountAccess;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/account-access")
@RestController
public class AccountAccessController {

    @PostMapping
    public void addAccess(@RequestBody AccountAccess accountAccess) {}
    @PutMapping
    public void changeAccess(@RequestBody AccountAccess accountAccess) {

    }
    @DeleteMapping
    public AccountAccess deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        return new AccountAccess(
                accountId,
                userId,
                true,
                true
        );
    }
}
