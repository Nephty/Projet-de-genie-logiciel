package com.example.demo.controller;


import com.example.demo.model.Account;
import com.example.demo.request.AccountReq;
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

    /**
     * @param iban [path] iban of the requested account
     * @return account with matching iban
     * 200 - OK
     * 404 - Not Found
     */
    @GetMapping(value = "{iban}")
    public ResponseEntity<Account> sendAccount(@PathVariable String iban) {
        return new ResponseEntity<>(accountService.getAccount(iban), HttpStatus.OK);
    }

    /**
     * @param accountReq [body] account to add to the DB
     * @return account to String
     * 201 - Created
     * 400 - Bad Request
     */
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountReq accountReq){
        log.info("incoming account: {}", accountReq.toString());
        accountService.addAccount(accountReq);
        return new ResponseEntity<>(accountReq.toString(), HttpStatus.CREATED);
    }

    /**
     * @param iban [path] iban of the account to be deleted
     * @return iban sent
     * 200 - OK
     */
    @DeleteMapping(value = "{iban}")
    public ResponseEntity<String> deleteAccount(@PathVariable String iban) {
        accountService.deleteAccount(iban);
        return new ResponseEntity<>(iban, HttpStatus.OK);
    }
}


