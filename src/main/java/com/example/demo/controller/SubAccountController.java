package com.example.demo.controller;


import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.SubAccount;
import com.example.demo.request.SubAccountReq;
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

    /**
     * Get a certain SubAccount.
     *
     * <br>Http codes.
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     *
     * @param iban       iban of the sub account to retrieve
     * @param currencyId currency of the sub account desired
     * @return the sub account matching the params provided
     */
    @GetMapping
    public ResponseEntity<SubAccountReq> getSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        SubAccountReq subAccountReq = subAccountService.getSubAccount(iban, currencyId);
        return new ResponseEntity<>(subAccountReq, HttpStatus.OK);
    }

    /**
     * Add a certain SubAccount to the DB.
     *
     * <br>Http codes.
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad Format</li>
     *     <li>409 - Bad FK</li>
     * </ul>
     * Who ? owner of the account
     *
     * @param subAccountReq [body] {@link SubAccount} to be added to the DB
     * @return {@link SubAccount} to String
     */
    @PostMapping
    public ResponseEntity<String> addSubAccount(@RequestBody SubAccountReq subAccountReq) {
        if (!subAccountReq.isPostValid()) throw new MissingParamException();

        SubAccount savedSubAccount = subAccountService.addSubAccount(subAccountReq);
        return new ResponseEntity<>(savedSubAccount.toString(), HttpStatus.CREATED);
    }

    /**
     * Deletes a SubAccount.
     *
     * <br> Http codes :
     * <ul>
     *     <li>200 - ok</li>
     * </ul>
     * Who ? owner of the SubAccount and maybe the bank
     *
     * @param iban       [param] id of the account linked
     * @param currencyId [param] currency of this SubAccount
     * @return params sent
     */
    @DeleteMapping
    public ResponseEntity<String> deleteSubAccount(@RequestParam String iban, @RequestParam Integer currencyId) {
        subAccountService.deleteSubAccount(iban, currencyId);
        return new ResponseEntity<>(iban + " : " + currencyId, HttpStatus.OK);
    }

    /**
     * Modify a SubAccount
     *
     * <br>Http codes.
     * <ul>
     *     <li>201 - Created</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? owner of the SubAccount
     * @param subAccountReq [body] SubAccount to be changed in the DB
     * @return SubAccount to String
     */
    @PutMapping
    public ResponseEntity<String> changeSubAccount(@RequestBody SubAccountReq subAccountReq) {
        if (!subAccountReq.isPutValid()) throw new MissingParamException();

        SubAccount savedSubAccount = subAccountService.changeSubAccount(subAccountReq);
        return new ResponseEntity<>(savedSubAccount.toString(), HttpStatus.CREATED);
    }
}
