package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.AccountAccessReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class AccountAccessService {
    private final AccountAccessRepo accountAccessRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    public AccountAccess createAccountAccess(AccountAccessReq accountAccessReq) {
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq);
        return accountAccessRepo.save(accountAccess);
    }

    public AccountAccess changeAccountAccess(AccountAccessReq accountAccessReq) {
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq);
        return accountAccessRepo.save(accountAccess);
    }

    public void deleteAccountAccess(String accountId, String userId) {
        accountAccessRepo.deleteById(new AccountAccessPK(accountId, userId));
    }

    public AccountAccess findAccountAccess(String accountId,String userId){
        return accountAccessRepo.findById(new AccountAccessPK(accountId, userId))
                .orElseThrow(()-> new ResourceNotFound(accountId + " : " + userId));
    }

    public List<AccountAccess> getAccountAccessByUserId(String userID){
        return accountAccessRepo.getAllByUserId(userID);
    }

    private AccountAccess instantiateAccountAccess(AccountAccessReq accountAccessReq) {
        AccountAccess accountAccess = new AccountAccess(accountAccessReq);
        Account account = accountRepo.findById(accountAccessReq.getAccountId())
                .orElseThrow(()-> new ConflictException(accountAccessReq.getAccountId()));
        User user = userRepo.findById(accountAccessReq.getUserId())
                .orElseThrow(()-> new ConflictException(accountAccessReq.getUserId()));
        accountAccess.setAccountId(account);
        accountAccess.setUserId(user);
        return accountAccess;
    }
}
