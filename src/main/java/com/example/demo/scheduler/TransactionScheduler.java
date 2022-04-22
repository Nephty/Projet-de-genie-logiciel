package com.example.demo.scheduler;

import com.example.demo.model.TransactionLog;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.service.NotificationService;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler extends AbstractScheduler {

    private final TransactionLogRepo transactionLogRepo;
    private final NotificationService notificationService;
    private final TransactionLogService transactionLogService;

    @Scheduled(initialDelay = 30, fixedRate = day, timeUnit = TimeUnit.SECONDS)
    public void performDueTransactions() {
        ArrayList<TransactionLog> transactionsToPerform = transactionLogRepo.findAllToExecute();
        log.info("[SCHEDULED TASK] Performing transactions (n={})", transactionsToPerform.size());
        if(transactionsToPerform.size() % 2 != 0) {
            //Shouldn't be happening but if it happens we have to manage it correctly.
            ArrayList<TransactionLog> badFormatTransaction = transactionLogRepo.findBadFormatTransaction();
            for (TransactionLog t : badFormatTransaction){
                transactionLogRepo.deleteAllByTransactionId(t.getTransactionId());
                transactionLogService.sendBadFormatTransaction(t);
            }
        }
        else {
            for (int i = 0; i < transactionsToPerform.size(); i += 2) {
                TransactionLog transactionA = transactionsToPerform.get(i);
                TransactionLog transactionB = transactionsToPerform.get(i + 1);

                transactionLogService.executeTransaction(
                        // -- Sender
                        transactionA.getIsSender() ? transactionA : transactionB,
                        // -- Receiver
                        transactionA.getIsSender() ? transactionB : transactionA
                );
            }
            log.info("[SCHEDULED TASK] Completed scheduled task");
        }
    }
}
