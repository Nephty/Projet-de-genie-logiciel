package com.example.demo.service;

import com.example.demo.controller.AccountController;
import com.example.demo.exception.throwables.AuthorizationException;
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


/**
 * Links the {@link AccountController} with the {@link AccountRepo}.
 * In this class, all the modifications and the calls to the {@link AccountRepo} are made.
 *
 * @see Account
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AccountService {

    private final AccountRepo accountRepo;

    private final BankRepo bankRepo;

    private final UserRepo userRepo;

    private final AccountTypeRepo accountTypeRepo;

    private final SubAccountRepo subAccountRepo;

    private final AccountAccessRepo accountAccessRepo;

    /**
     * Find the account with his iban
     *
     * @param iban The iban of the account
     * @return The req body of the Account with the good iban.
     * @throws ResourceNotFound if the account doesn't exist.
     */
    public AccountReq getAccount(String iban) throws ResourceNotFound {
        Account account = accountRepo.findById(iban).orElseThrow(
                () -> new ResourceNotFound("iban: " + iban)
        );
        return new AccountReq(account);
    }

    /**
     * Delete the account in the DB. <br>
     * An account is deleted by setting his parameter "deleted" to true because we don't want it to be deleted
     * to still have access to his history.
     *
     * @param iban the iban of the account
     * @return The account deleted
     * @throws ResourceNotFound if the account doesn't exist)
     */
    public Account deleteAccount(String iban) throws ResourceNotFound {
        // To delete an account, we set the delete parameter to true
        Account account = accountRepo.safeFindById(iban).orElseThrow(
                ()-> new ResourceNotFound("iban: " + iban)
        );
        account.setDeleted(true);
        return accountRepo.save(account);
    }

    /**
     * Creates an account and saves it in the DB.
     * Also create a default access and a default subAccount.
     *
     * @param accountReq The req body to create an account. ({@link AccountReq#isPostValid()})
     * @return The created account.
     * @throws AuthorizationException We can't create a fixed account with the payment authorisation.
     * @throws ConflictException      if the FK provided do not exist
     * @throws LittleBoyException     if the method isn't PUT or POST
     */
    public Account addAccount(AccountReq accountReq) throws AuthorizationException {
        if (accountReq.getAccountTypeId() == 4 && accountReq.getPayment()) {
            throw new AuthorizationException("This is a fixed account you can't allow payment to it");
        }
        Account account = instantiateAccount(accountReq, HttpMethod.POST);

        switch (account.getAccountTypeId().getAccountTypeId()) {
            case 1:
            case 3:
            case 4:
                boolean isAdult = account.getUserId().isAbove18();
                if(!isAdult) throw new AuthorizationException(
                        "You must be above 18 to register for that account"
                );
                break;
            case 2:
                if(account.getUserId().getAge() > 24) throw new AuthorizationException(
                        "You must be younger than 25 to register for a young account"
                );
                break;
            default:
                throw new LittleBoyException("This account type is not yet supported");
        }
        SubAccount defaultSubAccount = SubAccount.createDefault(account);
        AccountAccess defaultAccountAccess = AccountAccess.createDefault(account);
        accountRepo.save(account);
        subAccountRepo.save(defaultSubAccount);
        accountAccessRepo.save(defaultAccountAccess);
        return account;
    }

    /**
     * Changes the account and saves it in the db.
     *
     * @param accountReq The req body to change the account ({@link AccountReq#isPutValid()})
     * @return The changed account
     * @throws AuthorizationException Can't accept payment to fixed account.
     * @throws ResourceNotFound       if the account that the client tries to change doesn't exist
     * @throws LittleBoyException     if the method isn't PUT or POST
     */
    public Account changeAccount(AccountReq accountReq) throws AuthorizationException {
        Account account = instantiateAccount(accountReq, HttpMethod.PUT);
        //Fixed account
        if (account.getAccountTypeId().getAccountTypeId() == 4 && accountReq.getPayment()) {
            throw new AuthorizationException("This is a fixed account you can't allow payment to it");
        }
        //young account
        if(account.getAccountTypeId().getAccountTypeId() == 2 && accountReq.getPayment()) {
            if(accountAccessRepo.getAllOwners(account).stream()
                    .noneMatch(
                            user -> user.isAbove18()
                    )
            ){
                throw new AuthorizationException(
                        "An adult needs to have access to this account in order for you to make payment with it"
                );
            }
        }
        return accountRepo.save(account);
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     *
     * @param accountReq incoming req
     * @param method     method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException  if the FK provided do not exist
     * @throws ResourceNotFound   if the account that the client tries to change doesn't exist
     * @throws LittleBoyException if the method isn't PUT or POST
     */
    private Account instantiateAccount(AccountReq accountReq, HttpMethod method) throws ConflictException, ResourceNotFound, LittleBoyException {
        Account account;
        switch (method) {
            case POST:
                account = new Account(accountReq);
                Bank bank = bankRepo.findById(accountReq.getSwift())
                        .orElseThrow(() -> new ConflictException(accountReq.getSwift()));
                User user = userRepo.findById(accountReq.getUserId())
                        .orElseThrow(() -> new ConflictException(accountReq.getUserId()));
                AccountType accountType = accountTypeRepo.findById(accountReq.getAccountTypeId())
                        .orElseThrow(() -> new ConflictException(accountReq.getAccountTypeId().toString()));
                account.setSwift(bank);
                account.setUserId(user);
                account.setAccountTypeId(accountType);
                break;
            case PUT:
                account = accountRepo.safeFindById(accountReq.getIban())
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
