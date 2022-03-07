package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/account-access")
@RestController
public class AccountAccessController {

    @PostMapping
    public void addAccess(@RequestBody AccountAccess accountAccess) {
        throw new UnimplementedException();
    }
    @PutMapping
    public void changeAccess(@RequestBody AccountAccess accountAccess) {
        throw new UnimplementedException();
    }
    @DeleteMapping
    public AccountAccess deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        return new AccountAccess(
                new Account(),
                new User(),
                true,
                true
        );
    }
}
