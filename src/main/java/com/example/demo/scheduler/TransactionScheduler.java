package com.example.demo.scheduler;

import com.example.demo.model.TransactionLog;
import com.example.demo.repository.TransactionLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler {

    private final long minute = 60;
    private final long hour = 60 * minute;
    private final long day = 24 * hour;

    private final TransactionLogRepo transactionLogRepo;

    @Scheduled(initialDelay = 5, fixedRate = day)
    public void performDueTransactions() {
        ArrayList<TransactionLog> transactionsToPerform = transactionLogRepo.findAllToExecute();
        log.info("[SCHEDULED TASK] Performing transactions (n={})", transactionsToPerform.size());
        if(transactionsToPerform.size() % 2 != 0) {
            //an error occurred and needs to be fixed manually
            log.error("Could not perform the transaction because a pair is incomplete");
            return;
        }
        for(int i = 0; i < transactionsToPerform.size(); i+= 2) {
            TransactionLog transactionA = transactionsToPerform.get(i);
            TransactionLog transactionB = transactionsToPerform.get(i + 1);
            if(assertFromSameTransfer(transactionA, transactionB)
                    //the sending transaction has a direction of 1
                    && assertCanMakeTransaction(transactionA.getDirection() == 1 ? transactionA : transactionB)
            ) {
                performAndSaveTransaction(
                        transactionA.getDirection() == 1 ? transactionA : transactionB,
                        transactionA.getDirection() == 0 ? transactionA : transactionB
                );
            }
        }
        log.info("[SCHEDULED TASK] Completed scheduled task");
    }

    private boolean assertFromSameTransfer(TransactionLog transactionA, TransactionLog transactionB) {
        //Remove after making sure the query works
        if(transactionA.getTransactionDate().after(new Date(System.currentTimeMillis()))) {
            log.error("Do better SQL plz");
            return false;
        }
        // not the same id or same direction -> inconsistent state
        if(transactionA.getTransactionId().intValue() != transactionB.getTransactionId().intValue()
                || transactionA.getDirection().intValue() == transactionB.getDirection().intValue()
        ) {
            log.error("[SCHEDULE]Error matching the id of transaction pair {}, {}",
                    transactionA.getTransactionId(),
                    transactionB.getTransactionId()
            );
            //transactionLogRepo.deleteAllByTransactionId(transactionA.getTransactionId());
            //transactionLogRepo.deleteAllByTransactionId(transactionB.getTransactionId());
            return false;
        }
        return true;
    }

    private boolean assertCanMakeTransaction(TransactionLog transactionSent) {
        if(!transactionSent.getSubAccount().getIban().getPayment()) {
            log.warn("[SCHEDULE]This account can't make payment {}", transactionSent.getTransactionId());
            //transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            return false;
        }
        if(transactionSent.getTransactionAmount() <= 0) {
            log.warn("[SCHEDULE]Can't make transaction lower or equal to 0, {}", transactionSent.getTransactionId());
            //transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            return false;
        }
        if(transactionSent.getSubAccount().getCurrentBalance() < transactionSent.getTransactionAmount()) {
            log.warn("[SCHEDULE]Not enough fund {}", transactionSent.getTransactionId());
            //transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            return false;
        }
        return true;
    }

    private void performAndSaveTransaction(TransactionLog transactionSent, TransactionLog transactionReceived) {
        log.info("before: {} {}", transactionSent.getSubAccount().getCurrentBalance(), transactionReceived.getSubAccount().getCurrentBalance());
        transactionSent.getSubAccount().setCurrentBalance(
                transactionSent.getSubAccount().getCurrentBalance() - transactionSent.getTransactionAmount()
        );
        transactionReceived.getSubAccount().setCurrentBalance(
                transactionReceived.getSubAccount().getCurrentBalance() + transactionSent.getTransactionAmount()
        );
        log.info("after: {} {}", transactionSent.getSubAccount().getCurrentBalance(), transactionReceived.getSubAccount().getCurrentBalance());
        transactionSent.setProcessed(true);
        transactionReceived.setProcessed(true);
        transactionLogRepo.saveAll(Arrays.asList(transactionSent, transactionReceived));
    }
}