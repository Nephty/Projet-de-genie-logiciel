package com.example.demo.service;

import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.repository.AccountAccessRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class AccountAccessService {
    private final AccountAccessRepo accountAccessRepo;
    public void createAccountAccess(AccountAccess accountAccess) {
        accountAccessRepo.save(accountAccess);
    }

    public void changeAccountAccess(AccountAccess accountAccess) {
        accountAccessRepo.save(accountAccess);
    }

    public void deleteAccountAccess(String accountId, String userId) {
        accountAccessRepo.deleteById(new AccountAccessPK(accountId, userId));
    }
}
