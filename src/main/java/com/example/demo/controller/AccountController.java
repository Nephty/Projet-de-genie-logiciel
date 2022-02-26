package com.example.demo.controller;

import com.example.demo.exception.throwables.AuthenticationException;
import com.example.demo.model.Account;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/account")
@RestController
public class AccountController {

    @GetMapping(value = "{iban}")
    public Account sendAccount(@PathVariable String iban) {
        throw new AuthenticationException("not implemented");
    }

    @PostMapping
    public void createAccount(@RequestBody Account account) {
        throw new AuthenticationException("not implemented");
    }

    @DeleteMapping(value = "{iban}")
    public void deleteAccount(@PathVariable String iban) {
        throw new AuthenticationException("not implemented");
    }
}
