package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import com.example.demo.repository.AccountAccessRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class AccountAccessService {
    private final AccountAccessRepo accountAccessRepo;

    public void createAccountAccess(AccountAccess accountAccess) {
        // TODO verify that we didn't create that access already
        accountAccessRepo.save(accountAccess);
    }

    public void changeAccountAccess(AccountAccess accountAccess) {
        accountAccessRepo.save(accountAccess);
    }

    public void deleteAccountAccess(String accountId, String userId) {
        accountAccessRepo.deleteById(new AccountAccessPK(accountId, userId));
    }

    public AccountAccess getAccountAccess(String accountId,String userId){
        return accountAccessRepo.getById(new AccountAccessPK(accountId,userId));
    }

    public List<AccountAccess> getAccountAccessByUserId(String userID){
        return accountAccessRepo.getAllByUserId(userID);
    }
}
