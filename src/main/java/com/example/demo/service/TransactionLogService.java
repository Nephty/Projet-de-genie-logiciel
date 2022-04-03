package com.example.demo.service;

import com.example.demo.exception.throwables.*;
import com.example.demo.model.*;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.CompositePK.SubAccountPK;
import com.example.demo.other.Sender;
import com.example.demo.repository.AccountAccessRepo;
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
    private final AccountAccessRepo accountAccessRepo;

    public ArrayList<TransactionLog> addTransaction(Sender sender, TransactionReq transactionReq) {
        ArrayList<TransactionLog> transactions = instantiateTransaction(sender, transactionReq);
        ArrayList<TransactionLog> saved = new ArrayList<>();
        transactions.forEach(transaction -> saved.add(transactionLogRepo.save(transaction)));
        return saved;
    }

    /**
     * This method both retrieves and format the transaction from a subAccount
     * in the DB the transaction are in pairs, one ascending and one descending this method regroups the pairs into one
     * entity
     * @param iban subAccount iban
     * @param currencyId subAccount currency
     * @return An array of all the transaction made and received by the subAccount
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
    private ArrayList<TransactionLog> instantiateTransaction(
            Sender sender,
            TransactionReq transactionReq
    ) throws ConflictException{
        if(transactionReq.getSenderIban().equals(transactionReq.getRecipientIban())) {
            log.warn("transaction to = transaction from");
            throw new AuthorizationException("You can't make a transaction to the same account you emitted it");
        }
        TransactionLog transactionSent = new TransactionLog(transactionReq);
        TransactionLog transactionReceived = new TransactionLog(transactionReq);


        SubAccount subAccountSender = subAccountRepo.findById(
                new SubAccountPK(transactionReq.getSenderIban(), transactionReq.getCurrencyId())
        ).orElseThrow(
                ()-> new ConflictException(
                        "subAccount: " + transactionReq.getSenderIban() + " : " + transactionReq.getCurrencyId()
                )
        );
        // TODO : Do the transaction once a day
        //  and check everything when it's done and not when we instantiation the transaction

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


        // -- SENDER --
        transactionSent.setSubAccount(subAccountSender);
        transactionSent.setTransactionTypeId(transactionType);
        transactionSent.setDirection(1);

        // -- RECEIVER --
        transactionReceived.setSubAccount(subAccountReceiver);
        transactionReceived.setTransactionTypeId(transactionType);
        transactionReceived.setDirection(0);

        assertCanMakeTransaction(transactionSent);
        assertSenderHaveAccess(sender,transactionSent);

        // -- ID GENERATION --
        Integer nextId = nextId();
        transactionSent.setTransactionId(nextId);
        transactionReceived.setTransactionId(nextId);

        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        transactionLogs.add(transactionSent);
        transactionLogs.add(transactionReceived);

       /*
        subAccountSender.setCurrentBalance(
                subAccountSender.getCurrentBalance() - transactionReq.getTransactionAmount()
        );
        subAccountReceiver.setCurrentBalance(
                subAccountReceiver.getCurrentBalance() + transactionReq.getTransactionAmount()
        );
        */
        return transactionLogs;
    }

    // TODO: 4/3/22 JAVADOC
    private void
    assertCanMakeTransaction(TransactionLog transaction) {
        if(!transaction.getSubAccount().getIban().getPayment()) {
            log.warn("This account can't make payment");
            throw new AuthorizationException("This account can't make payment");
        }
        if(transaction.getTransactionAmount() <= 0) {
            throw new AuthorizationException("Can't make transaction lower or equal to 0");
        }
        if(transaction.getSubAccount().getCurrentBalance() < transaction.getTransactionAmount()) {
            throw new AuthorizationException("Not enough fund");
        }
    }

    // TODO: 4/3/22 Javadoc
    private void assertSenderHaveAccess(Sender sender, TransactionLog transaction){
        //when the sender is not the account owner
        if(!transaction.getSubAccount().getIban().getUserId().getUserId().equals(sender.getId())) {
            AccountAccess accountAccess = accountAccessRepo.findById(
                    new AccountAccessPK(transaction.getSubAccount().getIban().getIban(), sender.getId())
            ).orElseThrow(()-> {
                log.warn("User doesn't have access to this account");
                throw new AuthorizationException("You don't have access to this account");
            });
            if(!accountAccess.getAccess()) {
                log.warn("User access to this account is disabled");
                throw new AuthorizationException("Your access to this account is disabled");
            }
        }
    }


    /**
     *
     * Execute the transaction from the subAccount of the sender to the SubAccount of the Receiver.
     * If the transaction couldn't be executed, it throws an exception.
     * @see #assertCanMakeTransaction(TransactionLog transaction) assertCanMakeTransaction
     * @param transactionSent Sender transaction (Direction = 1)
     * @param transactionReceive Receiver transaction (Direction = 0)
     */
    public void executeTransaction(TransactionLog transactionSent, TransactionLog transactionReceive){
        assertCanMakeTransaction(transactionSent);

        transactionSent.getSubAccount().setCurrentBalance(
                transactionSent.getSubAccount().getCurrentBalance() - transactionSent.getTransactionAmount()
        );
        transactionSent.setProcessed(true);
        transactionReceive.getSubAccount().setCurrentBalance(
                transactionReceive.getSubAccount().getCurrentBalance() + transactionReceive.getTransactionAmount()
        );
        transactionReceive.setProcessed(true);

        transactionLogRepo.save(transactionSent);
        transactionLogRepo.save(transactionReceive);
    }

    /**
     * Find the maximum transactionId in the database and return the next value.
     * @return the next possible transactionIf
     */
    private int nextId(){
        Integer tmp = transactionLogRepo.findMaximumId();
        return tmp == null ? 1 : tmp+1;
    }
}