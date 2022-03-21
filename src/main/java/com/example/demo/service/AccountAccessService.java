package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class AccountAccessService {
    private final AccountAccessRepo accountAccessRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    public AccountAccess createAccountAccess(AccountAccessReq accountAccessReq) {
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq, HttpMethod.POST);
        return accountAccessRepo.save(accountAccess);
    }

    public AccountAccess changeAccountAccess(AccountAccessReq accountAccessReq) {
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq, HttpMethod.PUT);
        return accountAccessRepo.save(accountAccess);
    }

    public void deleteAccountAccess(String accountId, String userId) {
        // DO NOT use the deleteById method because it generates an SQL error
        accountAccessRepo.deleteAccountAccessByAccountIdAndUserId(accountId,userId);
    }

    public AccountAccess findAccountAccess(String accountId,String userId){
        return accountAccessRepo.findById(new AccountAccessPK(accountId, userId))
                .orElseThrow(()-> new ResourceNotFound(accountId + " : " + userId));
    }

    public List<AccountAccess> getAccountAccessByUserId(String userID){
        return accountAccessRepo.getAllByUserId(userID);
    }

    public List<User> getAllCustomers(String swift){
        return accountAccessRepo.getAllCustomersInBank(swift);
    }

    private AccountAccess instantiateAccountAccess(AccountAccessReq accountAccessReq, HttpMethod method) {
        AccountAccess accountAccess;
        switch (method) {
            case POST:
                if(accountAccessRepo.existsById(
                        new AccountAccessPK(accountAccessReq.getAccountId(), accountAccessReq.getUserId())
                )) {
                   throw new ConflictException("account already exist" + accountAccessReq);
                }
                accountAccess = new AccountAccess(accountAccessReq);
                Account account = accountRepo.findById(accountAccessReq.getAccountId())
                        .orElseThrow(()-> new ConflictException(accountAccessReq.getAccountId()));
                User user = userRepo.findById(accountAccessReq.getUserId())
                        .orElseThrow(()-> new ConflictException(accountAccessReq.getUserId()));
                accountAccess.setAccountId(account);
                accountAccess.setUserId(user);
                return accountAccess;
            case PUT:
                accountAccess = accountAccessRepo.findById(
                        new AccountAccessPK(accountAccessReq.getAccountId(), accountAccessReq.getUserId())
                ).orElseThrow(()-> {
                    log.error("no such account access: "+ accountAccessReq);
                    return new ConflictException(accountAccessReq.toString());
                });
                accountAccess.change(accountAccessReq);
                return accountAccess;
            default:
                log.error("unknown method:" + method);
                throw new LittleBoyException();

        }
    }
}
