package com.example.demo.controller;


import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.service.AccountAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping(path = "/api/account-access")
@RestController
@Slf4j
public class AccountAccessController {

    private final AccountAccessService accountAccessService;

    private final HttpServletRequest httpRequest;

    /**
     * Returns a list with all the account access for a certain user.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>400 - Bad request</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? the user
     *
     * @param userId id of the user
     * @return Array of account access
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<AccountAccessReq>> sendAccountAccess(
            @RequestParam String userId,
            @RequestParam Boolean deleted, @RequestParam Boolean hidden) {
        if (deleted) {
            // If we want hidden and deleted, it only returns the deleted.
            return new ResponseEntity<>(
                    accountAccessService.getAccessToDeletedAccount(userId),
                    HttpStatus.OK
            );
        }
        if (hidden) {
            return new ResponseEntity<>(
                    accountAccessService.getAccessToHiddenAccount(userId),
                    HttpStatus.OK
            );
        }
        // If hidden and deleted is false, return all access (used for portfolio)
        return new ResponseEntity<>(
                accountAccessService.getAccountAccessByUserId(userId),
                HttpStatus.OK
        );
    }

    /**
     * Returns a list with all the Owners for a certain account.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     *
     * @param iban iban of the account
     * @return Array of User
     */
    @GetMapping(value = "/all/co-owner")
    public ResponseEntity<List<User>> getAllOwners(@RequestParam String iban) {
        return new ResponseEntity<>(
                accountAccessService.findAllOwners(iban),
                HttpStatus.OK
        );
    }


    /**
     * Find a certain account access.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? the owner of the account and the bank
     *
     * @param userId    id of the user with the access
     * @param accountId id of the user account
     * @return account access matching params
     */
    @GetMapping(value = "{userId}/{accountId}")
    public ResponseEntity<AccountAccessReq> sendAccountAccess(
            @PathVariable String userId, @PathVariable String accountId
    ) {
        return new ResponseEntity<>(
                accountAccessService.findAccountAccess(accountId, userId),
                HttpStatus.OK
        );
    }

    /**
     * Add certain access to the DB.
     *
     * <br>Http codes :
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad format</li>
     *     <li>403 - Access forbidden</li>
     *     <li>409 - Conflict</li>
     * </ul>
     * Who ? the owner of the account and/or the bank
     *
     * @param accountAccessReq [body] account access to be added to the DB
     * @return account access to String
     */
    @PostMapping
    public ResponseEntity<String> addAccess(@RequestBody AccountAccessReq accountAccessReq) {
        Sender sender = (Sender) httpRequest.getAttribute(Sender.getAttributeName());
        if (!accountAccessService.bankOwnsAccount(sender, accountAccessReq.getAccountId()))
            throw new AuthorizationException("You don't manage this account");
        if (!accountAccessReq.isPostValid()) throw new MissingParamException();
        log.info("inserting account-access: {}", accountAccessReq);

        AccountAccess savedAccountAccess = accountAccessService.createAccountAccess(accountAccessReq);
        return new ResponseEntity<>(savedAccountAccess.toString(), HttpStatus.CREATED);
    }


    /**
     * Modify certain access.
     *
     * <br>Http Codes :
     * <ul>
     *     <li>201 - Created</li>
     *     <li>400 - Bad format</li>
     *     <li>404 - Not found</li>
     * </ul>
     * Who ? the owner of the account and/or the bank.
     *
     * @param accountAccessReq [body] account access to be changed in the DB
     * @return account access to String
     */
    @PutMapping
    public ResponseEntity<String> changeAccess(@RequestBody AccountAccessReq accountAccessReq) {
        if (!accountAccessReq.isPutValid()) throw new MissingParamException();

        AccountAccess savedAccountAccess = accountAccessService.changeAccountAccess(accountAccessReq);
        return new ResponseEntity<>(savedAccountAccess.toString(), HttpStatus.CREATED);
    }

    /**
     * Deletes certain access of the DB.
     *
     * <br>Http codes :
     * <ul>
     *     <li>200 - ok</li>
     *     <li>403 - Access forbidden</li>
     * </ul>
     * Who ? the owner of the account and/or the bank
     *
     * @param userId    id of the user with the access
     * @param accountId id of the user account
     * @return params sent
     */
    @DeleteMapping
    public ResponseEntity<String> deleteAccess(@RequestParam String accountId, @RequestParam String userId) {
        Sender sender = (Sender) httpRequest.getAttribute(Sender.getAttributeName());
        if (!accountAccessService.bankOwnsAccount(sender, accountId))
            throw new AuthorizationException("You don't manage this account");
        accountAccessService.deleteAccountAccess(accountId, userId);
        return new ResponseEntity<>(accountId + " : " + userId, HttpStatus.OK);
    }


}
