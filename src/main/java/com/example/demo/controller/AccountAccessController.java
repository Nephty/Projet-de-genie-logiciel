package com.example.demo.controller;


import com.example.demo.model.AccountAccess;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/account-access")
@RestController
public class AccountAccessController {

    private final AccountAccessService accountAccessService;

    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
        return new ResponseEntity<>(accountAccess.toString(), HttpStatus.CREATED);
    }
    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccess accountAccess) {
        accountAccessService.createAccountAccess(accountAccess);
        return new ResponseEntity<>(accountAccess.toString(), HttpStatus.CREATED);
    }
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId, HttpStatus.CREATED);
    }
}
