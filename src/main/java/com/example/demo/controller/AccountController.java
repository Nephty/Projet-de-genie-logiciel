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

    /**
     * @param iban [path] iban of the account to retrieve
     * @return account with matching id
     * 200 - OK
     */
    @GetMapping(value = "{iban}")
    public ResponseEntity<Account> sendAccount(@PathVariable String iban) {
        return new ResponseEntity<>(accountService.getAccount(iban), HttpStatus.OK);
    }

    /**
     * @param account [body] Account to be added to the DB
     * @return account to string
     * 201 - Created
     * 400 - Bad format
     */
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody Account account){
        log.info("incoming account: {}", account.toString());
        accountService.addAccount(account);
        return new ResponseEntity<>(account.toString(), HttpStatus.CREATED);
    }

    /**
     * @param iban id of the account to delete
     * @return id sent
     * 200 - OK
     */
    @DeleteMapping(value = "{iban}")
    public ResponseEntity<String> deleteAccount(@PathVariable String iban) {
        accountService.deleteAccount(iban);
        return new ResponseEntity<>(iban, HttpStatus.OK);
    }
}


