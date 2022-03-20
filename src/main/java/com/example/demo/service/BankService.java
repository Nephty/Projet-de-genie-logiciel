package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.exception.throwables.UserAlreadyExist;
import com.example.demo.model.Bank;
import com.example.demo.model.CurrencyType;
import com.example.demo.other.Sender;
import com.example.demo.repository.BankRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.BankReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class BankService {

    private final BankRepo bankRepo;

    private final PasswordEncoder passwordEncoder;

    private final CurrencyTypeRepo currencyTypeRepo;

    public Bank addBank(BankReq bankReq) {
        Bank bank = instantiateBank(null, bankReq, HttpMethod.POST);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    public void deleteBank(String swift) {
        bankRepo.deleteById(swift);
    }

    public Bank changeBank(Sender sender,BankReq bankReq) {
        Bank bank = instantiateBank(sender, bankReq, HttpMethod.PUT);
        bank.setPassword(passwordEncoder.encode(bank.getPassword()));
        return bankRepo.save(bank);
    }

    public Bank getBank(String swift) {
        return bankRepo.findById(swift)
                .orElseThrow(()-> new ResourceNotFound(swift));
    }

    public Bank getByLogin(String login) {
        return bankRepo.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFound(login));
    }

    public ArrayList<Bank> getAllBanks() {
         return new ArrayList<>(bankRepo.findAll());
    }

    private Bank instantiateBank(Sender sender, BankReq bankReq, HttpMethod method) {
        Bank bank;
        CurrencyType currencyType;
        switch (method) {
            case POST:
                if(bankRepo.existsById(bankReq.getSwift())) {
                    throw new UserAlreadyExist(UserAlreadyExist.Reason.SWIFT);
                }
                if(bankRepo.existsByName(bankReq.getName())) {
                    throw new UserAlreadyExist(UserAlreadyExist.Reason.NAME);
                }
                bank = new Bank(bankReq);
                currencyType = currencyTypeRepo
                        .findById(bankReq.getDefaultCurrencyType())
                        .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyType().toString()));
                bank.setDefaultCurrencyType(currencyType);
                return bank;
            case PUT:
                bank = bankRepo.findById(sender.getId())
                        .orElseThrow(()-> new ResourceNotFound(sender.getId()));
                bank.change(bankReq);
                if(bankReq.getDefaultCurrencyType() != null) {
                    currencyType = currencyTypeRepo
                            .findById(bankReq.getDefaultCurrencyType())
                            .orElseThrow(() -> new ConflictException(bankReq.getDefaultCurrencyType().toString()));
                    bank.setDefaultCurrencyType(currencyType);
                }
                return bank;
            default:
                log.error("Invalid method {}", method);
                throw new LittleBoyException();
        }
    }
}
