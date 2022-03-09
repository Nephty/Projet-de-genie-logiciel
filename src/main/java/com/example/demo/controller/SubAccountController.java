package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.SubAccount;
import com.example.demo.service.SubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(value = "/api/account/sub-account")
@RestController
public class SubAccountController {

    private final SubAccountService subAccountService;

    @PostMapping
    public void addSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.addSubAccount(subAccount);
    }

    @DeleteMapping
    public void deleteSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        subAccountService.deleteSubAccount(iban, currencyId);
    }

    @PutMapping
    public void changeSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.changeSubAccount(subAccount);
    }
}
