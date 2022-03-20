package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.request.AccountReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class AccountService {

    private final AccountRepo accountRepo;

    private final BankRepo bankRepo;

    private final UserRepo userRepo;

    private final AccountTypeRepo accountTypeRepo;

    private final SubAccountRepo subAccountRepo;

    public Account getAccount(String iban) {
        return accountRepo.findById(iban)
                .orElseThrow(
                        ()-> new ResourceNotFound("iban: " + iban)
                );
    }

    public void deleteAccount(String iban) {
        accountRepo.deleteById(iban);
    }

    public void addAccount(AccountReq accountReq) {
        Account account = instantiateAccount(accountReq);
        SubAccount defaultSubAccount = SubAccount.createDefault(account);
        accountRepo.save(account);
        subAccountRepo.save(defaultSubAccount);
    }

    public void changeAccount(AccountReq accountReq) {
        Account account = instantiateAccount(accountReq);
        accountRepo.save(account);
    }

    private Account instantiateAccount(AccountReq accountReq) {
        Account account = new Account(accountReq);
        Bank bank = bankRepo.findById(accountReq.getSwift())
                .orElseThrow(()-> new ConflictException(accountReq.getSwift()));
        User user = userRepo.findById(accountReq.getUserId())
                .orElseThrow(()-> new ConflictException(accountReq.getUserId()));
        AccountType accountType = accountTypeRepo.findById(accountReq.getAccountTypeId())
                .orElseThrow(()-> new ConflictException(accountReq.getAccountTypeId().toString()));
        account.setSwift(bank);
        account.setUserId(user);
        account.setAccountTypeId(accountType);

        return account;
    }
}
