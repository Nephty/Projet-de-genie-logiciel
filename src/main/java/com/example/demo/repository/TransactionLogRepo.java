package com.example.demo.repository;

import com.example.demo.model.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface TransactionLogRepo extends JpaRepository<TransactionLog, Integer> {
    ArrayList<TransactionLog> findAllByIban(String iban);
}
