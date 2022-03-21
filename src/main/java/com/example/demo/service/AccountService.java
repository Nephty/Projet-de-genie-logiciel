package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.request.AccountReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class AccountService {

    private final AccountRepo accountRepo;

    private final BankRepo bankRepo;

    private final UserRepo userRepo;

    private final AccountTypeRepo accountTypeRepo;

    private final SubAccountRepo subAccountRepo;

    public AccountReq getAccount(String iban) {
        Account account = accountRepo.findById(iban).orElseThrow(
                        ()-> new ResourceNotFound("iban: " + iban)
        );
        return new AccountReq(account);
    }

    public void deleteAccount(String iban) {
        accountRepo.deleteById(iban);
    }

    public Account addAccount(AccountReq accountReq) {
        Account account = instantiateAccount(accountReq, HttpMethod.POST);
        SubAccount defaultSubAccount = SubAccount.createDefault(account);
        accountRepo.save(account);
        subAccountRepo.save(defaultSubAccount);
        return account;
    }

    public Account changeAccount(AccountReq accountReq) {
        Account account = instantiateAccount(accountReq, HttpMethod.PUT);
        return accountRepo.save(account);
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     * @param accountReq incoming req
     * @param method method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException if the FK provided do not exist
     * @throws ResourceNotFound if the account that the client tries to change doesn't exist
     * @throws LittleBoyException if the method isn't PUT or POST
     */
    private Account instantiateAccount(AccountReq accountReq, HttpMethod method) throws ConflictException, ResourceNotFound, LittleBoyException{
        Account account;
        switch (method) {
            case POST:
                account = new Account(accountReq);
                Bank bank = bankRepo.findById(accountReq.getSwift())
                        .orElseThrow(()-> new ConflictException(accountReq.getSwift()));
                User user = userRepo.findById(accountReq.getUserId())
                        .orElseThrow(()-> new ConflictException(accountReq.getUserId()));
                AccountType accountType = accountTypeRepo.findById(accountReq.getAccountTypeId())
                        .orElseThrow(()-> new ConflictException(accountReq.getAccountTypeId().toString()));
                account.setSwift(bank);
                account.setUserId(user);
                account.setAccountTypeId(accountType);
                break;
            case PUT:
                account = accountRepo.findById(accountReq.getIban())
                        .orElseThrow(()-> {
                            log.error("no such account " + accountReq);
                            throw new ResourceNotFound(accountReq.toString());
                        });
                account.change(accountReq);
                break;
            default:
                log.error("unknown method" + method);
                throw new LittleBoyException();
        }
        return account;
    }
}
