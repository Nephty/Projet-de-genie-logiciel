package com.example.demo.service;

import com.example.demo.model.TransactionLog;
import com.example.demo.repository.TransactionLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service @Transactional
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;

    public void addTransaction(TransactionLog transactionLog) {
        transactionLogRepo.save(transactionLog);
    }

    public ArrayList<TransactionLog> getTransactionByIban(String iban) {
        return transactionLogRepo.findAllByIban(iban);
    }
}