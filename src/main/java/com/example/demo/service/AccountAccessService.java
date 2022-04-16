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

import java.util.List;
import java.util.stream.Collectors;

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
        accountAccessRepo.deleteAccountAccessByAccountIdAndUserId(accountId,userId);
    }

    public AccountAccessReq findAccountAccess(String accountId,String userId){
        AccountAccess accountAccess = accountAccessRepo.findById(new AccountAccessPK(accountId, userId))
                .orElseThrow(()-> new ResourceNotFound(accountId + " : " + userId));
        return new AccountAccessReq(accountAccess);
    }

    public List<AccountAccessReq> getAccessToDeletedAccount(String userID){
        User user = userRepo.findById(userID)
                .orElseThrow(()-> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllDeletedAccountByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    public List<AccountAccessReq> getAccessToHiddenAccount(String userID){
        User user = userRepo.findById(userID)
                .orElseThrow(()-> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllHiddenByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    public List<AccountAccessReq> getAccountAccessByUserId(String userID){
        User user = userRepo.findById(userID)
                .orElseThrow(()-> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    public List<User> findAllOwners(String accountId){ //TODO allow deleted ??
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new ResourceNotFound("No account with such id: "+accountId));
        return accountAccessRepo.getAllOwners(account);
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     * @param accountAccessReq incoming req
     * @param method method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException if the accountAccess already exists
     * @throws LittleBoyException if the method provided is not PUT or POST
     * @throws ResourceNotFound if the account that the client tries to modify doesn't exist
     */
    private AccountAccess instantiateAccountAccess(
            AccountAccessReq accountAccessReq,
            HttpMethod method
    ) throws ConflictException, LittleBoyException, ResourceNotFound{
        AccountAccess accountAccess;
        switch (method) {
            case POST:
                if(accountAccessRepo.existsById(
                        new AccountAccessPK(accountAccessReq.getAccountId(), accountAccessReq.getUserId())
                )) {
                   throw new ConflictException("account already exist " + accountAccessReq);
                }
                accountAccess = new AccountAccess(accountAccessReq);
                Account account = accountRepo.safeFindById(accountAccessReq.getAccountId())
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
                    return new ResourceNotFound(accountAccessReq.toString());
                });
                accountAccess.change(accountAccessReq);
                return accountAccess;
            default:
                log.error("unknown method:" + method);
                throw new LittleBoyException();

        }
    }
}
