package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class AccountService {

    private final AccountRepo accountRepo;

    public Account getAccount(String iban) {
        return accountRepo.findById(iban)
                .orElseThrow(
                        ()-> new ResourceNotFound("iban: " + iban)
                );
    }

    public void deleteAccount(String iban) {
        accountRepo.deleteById(iban);
    }

    public void addAccount(Account account) {
        accountRepo.save(account);
    }
}
