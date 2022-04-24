package com.example.demo.service;

import com.example.demo.controller.AccountAccessController;
import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.User;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Links the {@link  AccountAccessController} with the {@link AccountAccessRepo}.
 * In this class, all the modifications and the calls to the {@link AccountAccessRepo} are made.
 *
 * @see AccountAccess
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AccountAccessService {

    private final AccountAccessRepo accountAccessRepo;
    private final UserRepo userRepo;
    private final AccountRepo accountRepo;

    /**
     * Creates an access and saves it in the DB.
     * @param accountAccessReq The req body to create an access ({@link AccountAccessReq#isPostValid()})
     * @return The created access.
     * @throws ConflictException  if the accountAccess already exists
     * @throws LittleBoyException if the method provided is not PUT or POST
     */
    public AccountAccess createAccountAccess(AccountAccessReq accountAccessReq)
            throws ConflictException, LittleBoyException {
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq, HttpMethod.POST);
        return accountAccessRepo.save(accountAccess);
    }

    /**
     * Changes the account and saves it in the DB.
     * @param accountAccessReq The req body to change the account ({@link AccountAccessReq#isPutValid()})
     * @return The changed access.
     * @throws ResourceNotFound       if the account that the client tries to change doesn't exist
     * @throws LittleBoyException     if the method isn't PUT or POST
     */
    public AccountAccess changeAccountAccess(AccountAccessReq accountAccessReq)
            throws ResourceNotFound, LittleBoyException{
        AccountAccess accountAccess = instantiateAccountAccess(accountAccessReq, HttpMethod.PUT);
        return accountAccessRepo.save(accountAccess);
    }

    /**
     * Deletes access of a certain user to an account.
     * @param accountId The id of the account
     * @param userId The id of the user
     */
    public void deleteAccountAccess(String accountId, String userId) {
        accountAccessRepo.deleteAccountAccessByAccountIdAndUserId(accountId, userId);
    }

    /**
     * @param accountId The id of the account
     * @param userId The id of the user
     * @return The req body of the access.
     * @throws ResourceNotFound If the access doesn't exist
     */
    public AccountAccessReq findAccountAccess(String accountId, String userId)
            throws ResourceNotFound {
        AccountAccess accountAccess = accountAccessRepo.findById(new AccountAccessPK(accountId, userId))
                .orElseThrow(() -> new ResourceNotFound(accountId + " : " + userId));
        return new AccountAccessReq(accountAccess);
    }

    /**
     * Get all access of a user to deleted accounts.
     * The list is ordered by banks.
     * @param userID The id of the User
     * @return A list of req body of the access.
     * @throws ResourceNotFound if the user doesn't exit
     */
    public List<AccountAccessReq> getAccessToDeletedAccount(String userID)
            throws ResourceNotFound {
        User user = userRepo.findById(userID)
                .orElseThrow(() -> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllDeletedAccountByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all access of a user to hidden accounts.
     * The list is ordered by banks.
     * @param userID The id of the User
     * @return A list of req body of the access.
     * @throws ResourceNotFound if the user doesn't exit
     */
    public List<AccountAccessReq> getAccessToHiddenAccount(String userID)
            throws ResourceNotFound{
        User user = userRepo.findById(userID)
                .orElseThrow(() -> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllHiddenByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all access of a user to accounts that aren't hidden or deleted.
     * The list is ordered by banks.
     * @param userID The id of the User
     * @return A list of req body of the access.
     * @throws ResourceNotFound if the user doesn't exit
     */
    public List<AccountAccessReq> getAccountAccessByUserId(String userID)
            throws ResourceNotFound{
        User user = userRepo.findById(userID)
                .orElseThrow(() -> {
                    log.warn("No user with such id: " + userID);
                    return new ResourceNotFound("No user with such id: " + userID);
                });
        return accountAccessRepo.findAllByUserId(user)
                .stream()
                .map(AccountAccessReq::new)
                .collect(Collectors.toList());
    }

    /**
     * Return all owners of an account (all users that have access to this account)
     * @param accountId the id of the account
     * @return A List of User that have access to this account
     * @throws ResourceNotFound If the account doesn't exist
     */
    public List<User> findAllOwners(String accountId) throws ResourceNotFound {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new ResourceNotFound("No account with such id: " + accountId));
        return accountAccessRepo.getAllOwners(account);
    }

    /**
     * Creates an entity based on the request that was made
     * The method vary depending on the http method
     *
     * @param accountAccessReq incoming req
     * @param method           method used either PUT or POST
     * @return An entity ready to be saved in the DB
     * @throws ConflictException  if the accountAccess already exists
     * @throws LittleBoyException if the method provided is not PUT or POST
     * @throws ResourceNotFound   if the account that the client tries to modify doesn't exist
     */
    private AccountAccess instantiateAccountAccess(
            AccountAccessReq accountAccessReq,
            HttpMethod method
    ) throws ConflictException, LittleBoyException, ResourceNotFound {
        AccountAccess accountAccess;
        switch (method) {
            case POST:
                if (accountAccessRepo.existsById(
                        new AccountAccessPK(accountAccessReq.getAccountId(), accountAccessReq.getUserId())
                )) {
                    throw new ConflictException("account already exist " + accountAccessReq);
                }
                accountAccess = new AccountAccess(accountAccessReq);
                Account account = accountRepo.safeFindById(accountAccessReq.getAccountId())
                        .orElseThrow(() -> new ConflictException(accountAccessReq.getAccountId()));

                User user = userRepo.findById(accountAccessReq.getUserId())
                        .orElseThrow(() -> new ConflictException(accountAccessReq.getUserId()));
                accountAccess.setAccountId(account);
                accountAccess.setUserId(user);
                return accountAccess;
            case PUT:
                accountAccess = accountAccessRepo.findById(
                        new AccountAccessPK(accountAccessReq.getAccountId(), accountAccessReq.getUserId())
                ).orElseThrow(() -> {
                    log.error("no such account access: " + accountAccessReq);
                    return new ResourceNotFound(accountAccessReq.toString());
                });
                accountAccess.change(accountAccessReq);
                return accountAccess;
            default:
                log.error("unknown method:" + method);
                throw new LittleBoyException();

        }
    }

    /**
     * Checks whether an account belongs to a bank
     * @param sender param included in the httpRequest containing the sender id
     * @param iban account to check
     * @return true if bank owns, false otherwise
     */
    public boolean bankOwnsAccount(Sender sender, String iban) {
        if (sender.getRole() != Role.BANK) {
            throw new AuthorizationException("Only bank can perform this request");
        }
        Account account = accountRepo.findById(iban)
                .orElseThrow(() -> new ResourceNotFound("Account doesn't exist"));

        return account.getSwift().getSwift().equals(sender.getId());
    }

}
