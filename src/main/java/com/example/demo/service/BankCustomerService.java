package com.example.demo.service;

import com.example.demo.model.BankCustomers;
import com.example.demo.model.CompositePK.BanksCustomersPK;
import com.example.demo.repository.BankCustomerRepo;
import com.example.demo.repository.BankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service @Transactional
public class BankCustomerService {
    private final BankCustomerRepo bankCustomerRepo;

    public void addCustomer(String swift, String customerId) {
        bankCustomerRepo.save(new BankCustomers(swift, customerId));
    }

    public void deleteCustomer(String swift, String customerId) {
        bankCustomerRepo.deleteById(new BanksCustomersPK(swift, customerId));
    }

    //TODO implement pagination
    public ArrayList<BankCustomers> getCustomers(String swift) {
        return bankCustomerRepo.findAllByBanksSwift(swift);
    }
}
