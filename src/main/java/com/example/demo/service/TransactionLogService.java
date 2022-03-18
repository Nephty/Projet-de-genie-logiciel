package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import com.example.demo.model.TransactionType;
import com.example.demo.repository.SubAccountRepo;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.repository.TransactionTypeRepo;
import com.example.demo.request.TransactionReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;
    private final SubAccountRepo subAccountRepo;
    private final TransactionTypeRepo transactionTypeRepo;

    public void addTransaction(TransactionReq transactionReq) {
        TransactionLog transactionLog = instantiateTransaction(transactionReq);
        transactionLogRepo.save(transactionLog);
    }

    public List<TransactionLog> getAllTransactionByIban(String iban) {
        SubAccount tmp = new SubAccount(new SubAccountPK(iban,0));
        return transactionLogRepo.findAllBySubAccount(tmp);
    }

    private TransactionLog instantiateTransaction(TransactionReq transactionReq) {
        TransactionLog transactionLog = new TransactionLog(transactionReq);

        SubAccount subAccount = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                ()-> new ConflictException("currId:" + transactionReq.getCurrencyId().toString())
        );

        TransactionType transactionType = transactionTypeRepo.findById(transactionReq.getTransactionTypeId())
                .orElseThrow(
                        ()-> new ConflictException("transId:" + transactionReq.getTransactionTypeId().toString())
                );
        transactionLog.setSubAccount(subAccount);
        transactionLog.setTransactionTypeId(transactionType);
        return transactionLog;
    }
}