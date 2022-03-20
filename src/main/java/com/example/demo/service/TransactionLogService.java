package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.model.CurrencyType;
import com.example.demo.model.SubAccount;
import com.example.demo.model.TransactionLog;
import com.example.demo.model.TransactionType;
import com.example.demo.other.Generator;
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
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service @Transactional @Slf4j
public class TransactionLogService {

    private final TransactionLogRepo transactionLogRepo;
    private final SubAccountRepo subAccountRepo;
    private final TransactionTypeRepo transactionTypeRepo;

    public ArrayList<TransactionLog> addTransaction(TransactionReq transactionReq) {
        ArrayList<TransactionLog> transactions = instantiateTransaction(transactionReq);
        ArrayList<TransactionLog> saved = new ArrayList<>();
        transactions.forEach(transaction -> {
            saved.add(transactionLogRepo.save(transaction));
        });
        return saved;
    }

    public List<TransactionReq> getAllTransactionBySubAccount(String iban, Integer currencyId) {
        SubAccount subAccount = subAccountRepo.findById(new SubAccountPK(iban, currencyId))
                .orElseThrow(()-> new ResourceNotFound(iban + " : " + currencyId.toString()));
        ArrayList<TransactionLog> transactionLogs = transactionLogRepo.findAllLinkedToSubAccount(subAccount);
        ArrayList<TransactionReq> response = new ArrayList<>();
        log.warn(transactionLogs.toString());

        // mapping the ugliness from the DB to a nicer response
        transactionLogs.forEach(transactionReceived -> {
            //if the direction is set at 0 then it is a received transaction
            if(transactionReceived.getDirection() == 0) {
                TransactionReq transactionReq = new TransactionReq();
                //extracting data from that transaction
                transactionReq.setRecipientIban(transactionReceived.getSubAccount().getIban().getIban());
                transactionReq.setTransactionAmount(transactionReceived.getTransactionAmount());
                transactionReq.setTransactionTypeId(transactionReceived.getTransactionTypeId().getTransactionTypeId());
                transactionReq.setCurrencyId(transactionReceived.getSubAccount().getCurrencyType().getCurrencyId());
                transactionReq.setCurrencyName(
                        transactionReceived.getSubAccount().getCurrencyType().getCurrency_type_name()
                );
                transactionReq.setRecipientName(
                        transactionReceived.getSubAccount().getIban().getUserId().getUsername()
                );
                transactionReq.setTransactionDate(transactionReceived.getTransaction_date());
                transactionReq.setTransactionId(transactionReceived.getTransactionId());
                //finding the matching transaction in the list to get the sender iban number
                transactionLogs.forEach(transactionSent -> {
                    if(Objects.equals(transactionSent.getTransactionId(), transactionReceived.getTransactionId())
                            && transactionSent.getDirection() == 1) {
                        transactionReq.setSenderIban(transactionSent.getSubAccount().getIban().getIban());
                        transactionReq.setSenderName(
                                transactionSent.getSubAccount().getIban().getUserId().getUsername()
                        );
                    }
                });
                if(transactionReq.getSenderIban() == null
                        || transactionReq.getRecipientIban() == null
                        || transactionReq.getTransactionAmount() == null
                        || transactionReq.getTransactionTypeId() == null
                ) {
                    log.error("error in retrieving the transaction log {} req {}", transactionReceived, transactionReq);
                    throw new ResourceNotFound("could not retrieve the transactions");
                }
                log.info("ADD");
                response.add(transactionReq);
            }
        });
        return response;
    }

    private ArrayList<TransactionLog> instantiateTransaction(TransactionReq transactionReq) {
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

        Integer rdmId = Generator.randomTransactionId();
        transactionSent.setTransactionId(rdmId);
        transactionReceived.setTransactionId(rdmId);

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
}