package com.example.demo.service;

import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.UserRepo;
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

    public AccountAccess createAccountAccess(String accountId, String userId, boolean access, boolean hidden) {

        ////If the user or the Account doesn't exist, this will throw an EntityNotFoundException
        Optional<AccountAccess> accountAccess_tmp = findAccountAccess(accountId,userId);
        if (accountAccess_tmp.isPresent()){
            throw new EntityExistsException("The access from the user "
                    +userId+" to the account "+accountId+" already exists");
        }

        //At this point we are sure that the Account and the User exists because we checked it above.
        AccountAccess newAccountAccess = new AccountAccess(
                accountRepo.getById(accountId),
                userRepo.getById(userId),
                access,
                hidden
        );
        accountAccessRepo.save(newAccountAccess);
        return newAccountAccess;
    }

    public AccountAccess changeAccountAccess(String accountId, String userId,boolean access, boolean hidden) {
        //If the user or the Account doesn't exist, this will throw an EntityNotFoundException
        Optional<AccountAccess> opt_account_access = findAccountAccess(accountId,userId);
        if (opt_account_access.isEmpty()){
            throw new EntityNotFoundException("There isn't any access from the user: "+userId
                    +" on the account: "+accountId);
        }


        AccountAccess new_account = opt_account_access.get();
        new_account.setAccess(access);
        new_account.setHidden(hidden);

        accountAccessRepo.save(new_account);
        return new_account;
    }

    public void deleteAccountAccess(String accountId, String userId) {
        Optional<User> user_tmp = userRepo.findByUserID(userId);
        Optional<Account> account_tmp = accountRepo.getAccountByIban(accountId);
        if (user_tmp.isEmpty()){
            throw new EntityNotFoundException("User with the userID : "+userId+" not found");
        }
        if (account_tmp.isEmpty()){
            throw new EntityNotFoundException("Account with the Iban : "+accountId+" not found");
        }

        accountAccessRepo.deleteAccountAccessByAccountIdAndUserId(account_tmp.get(),user_tmp.get());
    }

    public AccountAccess getAccountAccess(String accountId,String userId){
        return accountAccessRepo.getById(new AccountAccessPK(accountId,userId));
    }

    public Optional<AccountAccess> findAccountAccess(String accountId,String userId){
        Optional<User> user_tmp = userRepo.findByUserID(userId);
        Optional<Account> account_tmp = accountRepo.getAccountByIban(accountId);

        if (user_tmp.isEmpty()){
            throw new EntityNotFoundException("User with the userID : "+userId+" not found");
        }
        if (account_tmp.isEmpty()){
            throw new EntityNotFoundException("Account with the Iban : "+accountId+" not found");
        }

        return accountAccessRepo.getAccountAccessByUserIdAndAccountId(
                user_tmp.get(),
                account_tmp.get()
        );
    }

    public List<AccountAccess> getAccountAccessByUserId(String userID){
        return accountAccessRepo.getAllByUserId(userID);
    }
}
