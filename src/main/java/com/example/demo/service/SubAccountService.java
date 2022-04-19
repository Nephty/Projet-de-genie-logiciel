package com.example.demo.service;

import com.example.demo.controller.SubAccountController;
import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.AccountRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.request.SubAccountReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Links the {@link SubAccountController} with the {@link SubAccountRepo}.
 * In this class, all the modifications and the calls to the {@link SubAccountRepo} are made.
 *
 * @see SubAccount
 */
@RequiredArgsConstructor
@Service
@Transactional
public class SubAccountService {

    private final SubAccountRepo subAccountRepo;
    private final AccountRepo accountRepo;
    private final CurrencyTypeRepo currencyTypeRepo;

    /**
     * @param iban     The iban of the account
     * @param currency The id of the currency
     * @return The req body of the SubAccount
     * @throws ResourceNotFound if the SubAccount doesn't exist
     */
    public SubAccountReq getSubAccount(String iban, Integer currency)
            throws ResourceNotFound {
        SubAccount subAccount = subAccountRepo.findById(new SubAccountPK(iban, currency))
                .orElseThrow(() -> new ResourceNotFound(iban + " : " + currency));
        return new SubAccountReq(subAccount);
    }

    /**
     * Deletes a SubAccount in the DB.
     *
     * @param iban       The id of the subAccount
     * @param currencyId The id of the currency
     */
    public void deleteSubAccount(String iban, Integer currencyId) {
        subAccountRepo.deleteById(new SubAccountPK(iban, currencyId));
    }

    /**
     * Creates a SubAccount and stores it in the DB.
     *
     * @param subAccountReq The req body to create a SubAccount {@link SubAccountReq#isPostValid()}
     * @return The created SubAccount
     * @throws ConflictException if the FK provided by the client are incorrect
     */
    public SubAccount addSubAccount(SubAccountReq subAccountReq)
            throws ConflictException {
        SubAccount subAccount = instantiateSubAccount(subAccountReq);
        return subAccountRepo.save(subAccount);
    }

    /**
     * Changes a SubAccount and stores it in the DB
     *
     * @param subAccountReq The req body to change a SubAccount {@link SubAccountReq#isPutValid()}
     * @return The changed SubAccount
     * @throws ResourceNotFound if the SubAccount doesn't exist
     */
    public SubAccount changeSubAccount(SubAccountReq subAccountReq)
            throws ResourceNotFound {
        SubAccount subAccount = subAccountRepo.findById(
                new SubAccountPK(subAccountReq.getIban(), subAccountReq.getCurrencyType())
        ).orElseThrow(() -> new ResourceNotFound(subAccountReq.toString()));
        subAccount.change(subAccountReq);
        return subAccountRepo.save(subAccount);
    }

    /**
     * @param subAccountReq request made by the client
     * @return suBaccount entity based on the request
     * @throws ConflictException if the FK provided by the client are incorrect
     */
    private SubAccount instantiateSubAccount(SubAccountReq subAccountReq) throws ConflictException {
        SubAccount subAccount = new SubAccount(subAccountReq);
        Account account = accountRepo.safeFindById(subAccountReq.getIban())
                .orElseThrow(() -> new ConflictException(subAccountReq.getIban()));

        CurrencyType currencyType = currencyTypeRepo.findById(subAccountReq.getCurrencyType())
                .orElseThrow(() -> new ConflictException(subAccountReq.getCurrencyType().toString()));
        subAccount.setIban(account);
        subAccount.setCurrencyType(currencyType);
        return subAccount;
    }
}
