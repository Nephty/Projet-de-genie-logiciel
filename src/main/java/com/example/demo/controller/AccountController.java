package com.example.demo.controller;


import com.example.demo.json.AccountJson;
import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "{iban}")
    public Account sendAccount(@PathVariable String iban) {
        return accountService.getAccount(iban);
    }

    @PostMapping
    public void createAccount(@RequestBody Account account){
        accountService.addAccount(account);
    }

    @DeleteMapping(value = "{iban}")
    public void deleteAccount(@PathVariable String iban) {
        accountService.deleteAccount(iban);
    }
}


