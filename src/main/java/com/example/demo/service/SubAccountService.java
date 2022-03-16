package com.example.demo.service;

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

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class SubAccountService {

    private final SubAccountRepo subAccountRepo;
    private final AccountRepo accountRepo;
    private final CurrencyTypeRepo currencyTypeRepo;

    public SubAccount getSubAccount(String iban, Integer currency){
        return subAccountRepo.findById(new SubAccountPK(iban, currency))
                .orElseThrow(()-> new ResourceNotFound(iban + " : " + currency));
    }

    public void deleteSubAccount(String iban, Integer currencyId) {
        subAccountRepo.deleteById(new SubAccountPK(iban, currencyId));
    }

    public void addSubAccount(SubAccountReq subAccountReq) {
        SubAccount subAccount = instantiateSubAccount(subAccountReq);
        subAccountRepo.save(subAccount);
    }

    public void changeSubAccount(SubAccountReq subAccountReq) {
        SubAccount subAccount = instantiateSubAccount(subAccountReq);
        subAccountRepo.save(subAccount);
    }

    private SubAccount instantiateSubAccount(SubAccountReq subAccountReq) {
        SubAccount subAccount = new SubAccount(subAccountReq);
        Account account = accountRepo.findById(subAccountReq.getIban())
                .orElseThrow(()-> new ConflictException(subAccountReq.getIban()));
        CurrencyType currencyType = currencyTypeRepo.findById(subAccountReq.getCurrencyType())
                .orElseThrow(()-> new ConflictException(subAccountReq.getCurrencyType().toString()));
        subAccount.setIban(account);
        subAccount.setCurrencyType(currencyType);
        return subAccount;
    }
}
