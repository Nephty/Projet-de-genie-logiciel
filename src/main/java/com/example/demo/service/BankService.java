package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.BankReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service @Transactional
public class BankService {

    private final BankRepo bankRepo;

    private final CurrencyTypeRepo currencyTypeRepo;

    public void addBank(BankReq bankReq) {
        Bank bank = instantiateBank(bankReq);
        bankRepo.save(bank);
    }

    public void deleteBank(String swift) {
        bankRepo.deleteById(swift);
    }

    public void changeBank(BankReq bankReq) {
        Bank bank = instantiateBank(bankReq);
        bankRepo.save(bank);
    }

    public Bank getBank(String swift) {
        return bankRepo.getById(swift);
    }

    public ArrayList<Bank> getAllBanks() {
         return new ArrayList<>(bankRepo.findAll());
    }

    private Bank instantiateBank(BankReq bankReq) {
        Bank bank = new Bank(bankReq);
        CurrencyType currencyType = currencyTypeRepo
                .findById(bankReq.getDefaultCurrencyType())
                .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyType().toString()));
        bank.setDefaultCurrencyType(currencyType);
        return bank;
    }
}
