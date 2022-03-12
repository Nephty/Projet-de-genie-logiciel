package com.example.demo.service;

import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import com.example.demo.repository.TransactionLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;

    public void addTransaction(TransactionLog transactionLog) {
        transactionLogRepo.save(transactionLog);
    }

    public Optional<TransactionLog> getTransactionByIban(String iban) {
        SubAccount tmp = new SubAccount(new SubAccountPK(iban,0));
        return transactionLogRepo.findAllBySubAccount(tmp);
    }
}