package com.example.demo.service;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.SubAccount;
import com.example.demo.repository.SubAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service @Transactional
public class SubAccountService {

    private final SubAccountRepo subAccountRepo;

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
