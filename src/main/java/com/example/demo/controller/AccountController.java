package com.example.demo.controller;


import com.example.demo.exception.throwables.MissingParamException;
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
     * Send a certain account.
     * <br> Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     * @param iban [path] iban of the requested account
     * @return account with matching iban
     */
    @GetMapping(value = "{iban}")
    public ResponseEntity<AccountReq> sendAccount(@PathVariable String iban) {
        return new ResponseEntity<>(accountService.getAccount(iban), HttpStatus.OK);
    }

    /**
     * Creates a certain account and saves it in the DB.
     *
     * <br>Http codes :
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad request</li>
     *     <li>409 - Bad FK</li>
     * </ul>
     * Who ? bank
     *
     * @param accountReq [body] account to add to the DB
     * @return account to String
     */
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountReq accountReq){
        if(!accountReq.isPostValid()) throw new MissingParamException();

        Account savedAccount = accountService.addAccount(accountReq);
        return new ResponseEntity<>(savedAccount.toString(), HttpStatus.CREATED);
    }

    /**
     * Modify a certain account.
     *
     * <br>Http codes :
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad Request</li>
     *     <li>404 - Not found</li>
     *     <li>409 - Bad FK</li>
     * </ul>
     * Who ? the bank
     *
     * @param accountReq [body] account to change in the DB
     * @return account to String
     */
    @PutMapping
    public ResponseEntity<String> changeAccount(@RequestBody AccountReq accountReq){
        if(!accountReq.isPutValid()) throw new MissingParamException();

        Account savedAccount = accountService.changeAccount(accountReq);
        return new ResponseEntity<>(savedAccount.toString(), HttpStatus.CREATED);
    }

    /**
     * Deletes a certain account.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? the bank
     * @param iban [path] iban of the account to be deleted
     * @return iban sent
     */
    @DeleteMapping(value = "{iban}")
    public ResponseEntity<String> deleteAccount(@PathVariable String iban) {
        Account returnedAccount = accountService.deleteAccount(iban);
        return new ResponseEntity<>(returnedAccount.toString(), HttpStatus.OK);
    }
}


