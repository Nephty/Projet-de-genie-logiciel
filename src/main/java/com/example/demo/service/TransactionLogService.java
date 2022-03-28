package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.CompositePK.SubAccountPK;
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

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;
    private final SubAccountRepo subAccountRepo;
    private final TransactionTypeRepo transactionTypeRepo;

    public ArrayList<TransactionLog> addTransaction(TransactionReq transactionReq) {
        ArrayList<TransactionLog> transactions = instantiateTransaction(transactionReq);
        ArrayList<TransactionLog> saved = new ArrayList<>();
        transactions.forEach(transaction -> saved.add(transactionLogRepo.save(transaction)));
        return saved;
    }

    /**
     * This method both retrieves and format the transaction from a sub account
     * in the DB the transaction are in pairs, one ascending and one descending this method regroups the pairs into one
     * entity
     * @param iban sub account iban
     * @param currencyId sub account currency
     * @return An array of all the transaction made and received by the subaccount
     */
    public List<TransactionReq> getAllTransactionBySubAccount(String iban, Integer currencyId) {
        SubAccount subAccount = subAccountRepo.findById(new SubAccountPK(iban, currencyId))
                .orElseThrow(()-> new ResourceNotFound(iban + " : " + currencyId.toString()));
        ArrayList<TransactionLog> transactionLogs = transactionLogRepo.findAllLinkedToSubAccount(subAccount);
        if(transactionLogs.size() % 2 != 0) {
            log.error("inconsistent state for transaction table, subaccount: " + iban + " / " + currencyId);
        }
        ArrayList<TransactionReq> response = new ArrayList<>();
        // mapping the ugliness from the DB to a nicer response
        transactionLogs.stream()
                .filter(transactionLog -> transactionLog.getDirection() == 0)
                .forEach(transactionReceived -> {
                    transactionLogs.stream()
                            .filter(transactionLog -> transactionLog.getDirection() == 1)
                            .forEach(transactionSent -> {
                                if(transactionSent.getTransactionId().intValue()
                                        == transactionReceived.getTransactionId().intValue()) {
                                    response.add(new TransactionReq(transactionSent, transactionReceived));
                                }
                            });
                });
        return response;
    }


    /**
     * Creates a transaction entity and raise an error if the request is incorrect
     * @param transactionReq request made by the client
     * @return Transaction entity based on the client's request
     * @throws ConflictException if the FK provided by the client are inconsistent with the DB
     */
    private ArrayList<TransactionLog> instantiateTransaction(TransactionReq transactionReq) throws ConflictException{
        TransactionLog transactionSent = new TransactionLog(transactionReq);
        TransactionLog transactionReceived = new TransactionLog(transactionReq);


        SubAccount subAccountSender = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                ()-> new ConflictException(
                        "subAccount:" + transactionReq.getSenderIban() + " : " + transactionReq.getCurrencyId()
                )
        );
        if(subAccountSender.getCurrentBalance() < transactionReq.getTransactionAmount()) {
            throw new ConflictException("Not enough fund");
        }
        SubAccount subAccountReceiver = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getRecipientIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                ()-> new ConflictException(
                        "subAccount:" + transactionReq.getRecipientIban() + " : " + transactionReq.getCurrencyId()
                )
        );
        TransactionType transactionType = transactionTypeRepo.findById(transactionReq.getTransactionTypeId())
                .orElseThrow(
                        ()-> new ConflictException("transId:" + transactionReq.getTransactionTypeId().toString())
                );


        transactionSent.setSubAccount(subAccountSender);
        transactionSent.setTransactionTypeId(transactionType);
        transactionSent.setDirection(1);
        transactionReceived.setSubAccount(subAccountReceiver);
        transactionReceived.setTransactionTypeId(transactionType);
        transactionReceived.setDirection(0);

        Integer nextId = nextId();
        transactionSent.setTransactionId(nextId);
        transactionReceived.setTransactionId(nextId);

        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        transactionLogs.add(transactionSent);
        transactionLogs.add(transactionReceived);

        subAccountSender.setCurrentBalance(
                subAccountSender.getCurrentBalance() - transactionReq.getTransactionAmount()
        );
        subAccountReceiver.setCurrentBalance(
                subAccountReceiver.getCurrentBalance() + transactionReq.getTransactionAmount()
        );
        return transactionLogs;
    }

    private int nextId(){
        return transactionLogRepo.findMaximumId()+1;
    }
}