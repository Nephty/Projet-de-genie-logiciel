package com.example.demo.controller;

import com.example.demo.exception.throwables.UnimplementedException;
import com.example.demo.model.SubAccount;
import com.example.demo.service.SubAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(value = "/api/account/sub-account")
@RestController
public class SubAccountController {

    private final SubAccountService subAccountService;

    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.addSubAccount(subAccount);    
        return new ResponseEntity<>(subAccount.toString(), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        subAccountService.deleteSubAccount(iban, currencyId);
        return new ResponseEntity<>(iban + " : " + currencyId, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> changeSubAccount(@RequestBody SubAccount subAccount) {
        subAccountService.changeSubAccount(subAccount);
        return new ResponseEntity<>(subAccount.toString(), HttpStatus.CREATED);
    }
}
