package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.BankReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class BankService {

    private final BankRepo bankRepo;

    private final PasswordEncoder passwordEncoder;

    private final CurrencyTypeRepo currencyTypeRepo;

    public void addBank(BankReq bankReq) {
        alreadyExists(bankReq).ifPresent(userAlreadyExist -> {
            throw userAlreadyExist;
        });
        Bank bank = instantiateBank(bankReq);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        bankRepo.save(bank);
    }

    public void deleteBank(String swift) {
        bankRepo.deleteById(swift);
    }

    public void changeBank(BankReq bankReq) {
        alreadyExists(bankReq).orElseThrow(() -> new ResourceNotFound(bankReq.toString()));
        Bank bank = instantiateBank(bankReq);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        bankRepo.save(bank);
    }

    public Bank getBank(String swift) {
        return bankRepo.getById(swift);
    }

    public Bank getByLogin(String login) {
        return bankRepo.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFound(login));
    }

    public ArrayList<Bank> getAllBanks() {
         return new ArrayList<>(bankRepo.findAll());
    }

    private Optional<UserAlreadyExist> alreadyExists(BankReq bankReq) {
        if(bankRepo.existsById(bankReq.getSwift())) {
            return Optional.of(new UserAlreadyExist(UserAlreadyExist.Reason.SWIFT));
        }
        if(bankRepo.existsByName(bankReq.getName())) {
            return Optional.of(new UserAlreadyExist(UserAlreadyExist.Reason.NAME));
        }
        return Optional.empty();
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
