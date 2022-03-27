package com.example.demo.service;

import com.example.demo.exception.throwables.ConflictException;
import com.example.demo.exception.throwables.LittleBoyException;
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
        log.warn("fetching transaction for subaccount {}", subAccount);
        ArrayList<TransactionLog> transactionLogs = transactionLogRepo.findAllLinkedToSubAccount(subAccount);
        log.warn("[FROM DB]logs length: {}", transactionLogs.size());
        ArrayList<TransactionReq> response = new ArrayList<>();
        transactionLogs.forEach(transactionLog -> System.out.println(transactionLog.toSimpleString()));

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
                        transactionReceived.getSubAccount().getIban().getUserId().getFullName()
                );
                transactionReq.setTransactionDate(transactionReceived.getTransaction_date());
                transactionReq.setTransactionId(transactionReceived.getTransactionId());
                //finding the matching transaction in the list to get the sender iban number
                transactionLogs.forEach(transactionSent -> {
                    if((transactionSent.getTransactionId().intValue()
                            == transactionReceived.getTransactionId().intValue())
                            && transactionSent.getDirection() == 1) {
                        transactionReq.setSenderIban(transactionSent.getSubAccount().getIban().getIban());
                        transactionReq.setSenderName(
                                transactionSent.getSubAccount().getIban().getUserId().getFullName()
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
                        "subAccount: " + transactionReq.getSenderIban() + " : " + transactionReq.getCurrencyId()
                )
        );
        // TODO : Do the transaction once a day
        //  and check everything when it's done and not when we instantiation the transaction
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


        // -- SENDER --
        transactionSent.setSubAccount(subAccountSender);
        transactionSent.setTransactionTypeId(transactionType);
        transactionSent.setDirection(1);

        // -- RECEIVER --
        transactionReceived.setSubAccount(subAccountReceiver);
        transactionReceived.setTransactionTypeId(transactionType);
        transactionReceived.setDirection(0);

        // -- ID GENERATION --
        Integer nextId = nextId();
        transactionSent.setTransactionId(nextId);
        transactionReceived.setTransactionId(nextId);

        ArrayList<TransactionLog> transactionLogs = new ArrayList<>();
        transactionLogs.add(transactionSent);
        transactionLogs.add(transactionReceived);

        // TODO : do this once a day, and set the transaction type to done
        subAccountSender.setCurrentBalance(
                subAccountSender.getCurrentBalance() - transactionReq.getTransactionAmount()
        );
        subAccountReceiver.setCurrentBalance(
                subAccountReceiver.getCurrentBalance() + transactionReq.getTransactionAmount()
        );
        return transactionLogs;
    }

    private int nextId(){
        Integer tmp = transactionLogRepo.findMaximumId();
        return tmp == null ? 1 : tmp+1;
    }
}