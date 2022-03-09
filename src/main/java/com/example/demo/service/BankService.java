package com.example.demo.service;

import com.example.demo.model.Bank;
import com.example.demo.repository.BankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service @Transactional
public class BankService {

    private final BankRepo bankRepo;

    public void addBank(Bank bank) {
        bankRepo.save(bank);
    }

    public void deleteBank(String swift) {
        bankRepo.deleteById(swift);
    }

    public void changeBank(Bank bank) {
        bankRepo.save(bank);
    }

    public Bank getBank(String swift) {
        return bankRepo.getById(swift);
    }

    public ArrayList<Bank> getAllBanks() {
         return new ArrayList<>(bankRepo.findAll());
    }
}
