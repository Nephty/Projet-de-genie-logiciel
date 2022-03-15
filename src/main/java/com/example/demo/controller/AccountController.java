package com.example.demo.controller;


import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account")
@RestController @Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "{iban}")
    public ResponseEntity<Account> sendAccount(@PathVariable String iban) {
        return new ResponseEntity<>(accountService.getAccount(iban), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account){
        log.info("incoming account: {}", account.toString());
        accountService.addAccount(account);
        return new ResponseEntity<>(account.toString(), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{iban}")
    public ResponseEntity<String> deleteAccount(@PathVariable String iban) {
        accountService.deleteAccount(iban);
        return new ResponseEntity<>(iban, HttpStatus.OK);
    }
}


