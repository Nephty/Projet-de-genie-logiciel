package com.example.demo.repository;

import com.example.demo.model.BankCustomers;
import com.example.demo.model.CompositePK.BanksCustomersPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface BankCustomerRepo extends JpaRepository<BankCustomers, BanksCustomersPK> {
    ArrayList<BankCustomers> findAllByBanksSwift(String swift);
}
