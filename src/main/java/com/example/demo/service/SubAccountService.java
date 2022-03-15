package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.repository.SubAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class SubAccountService {

    private final SubAccountRepo subAccountRepo;
    private final CurrencyTypeService currencyTypeService;
    private final AccountService accountService;

    public SubAccount getSubAccount(String iban, Integer currency){
        CurrencyType currencyType = currencyTypeService.getCurrencyById(currency);
        Optional<SubAccount> subAccount= subAccountRepo.findByIbanAndCurrencyType(
                accountService.getAccount(iban),
                currencyType
        );
        if (subAccount.isEmpty()){
            throw new EntityNotFoundException("The subAccount of the account : "
                    +iban+" with the currency "+currencyType.getCurrency_type_name()+" doesn't exist");
        }
        return subAccount.get();
    }

    public boolean existsById(SubAccountPK subAccountPK){
        return subAccountRepo.existsById(subAccountPK);
    }

    public void deleteSubAccount(String iban, Integer currencyId) {
        subAccountRepo.deleteById(new SubAccountPK(iban, currencyId));
    }

    public void addSubAccount(SubAccount subAccount) {
        subAccountRepo.save(subAccount);
    }

    public void changeSubAccount(SubAccount subAccount) {
        subAccountRepo.save(subAccount);
    }
}
