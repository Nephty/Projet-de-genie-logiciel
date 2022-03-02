package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/account/sub-account")
@RestController
public class SubAccountController {
    @PostMapping
    public void addSubAccount() {
        throw new UnimplementedException();
    }

    @DeleteMapping
    public void deleteSubAccount(@RequestParam String iban, @RequestParam String currencyId) {
        throw new UnimplementedException();
    }

    @PutMapping
    public void changeSubAccount() {
        throw new UnimplementedException();
    }
}
