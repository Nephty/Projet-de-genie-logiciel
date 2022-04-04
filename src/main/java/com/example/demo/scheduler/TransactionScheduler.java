package com.example.demo.scheduler;

import com.example.demo.model.TransactionLog;
import com.example.demo.other.Sender;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.security.Role;
import com.example.demo.service.NotificationService;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler {

    private final long minute = 60;
    private final long hour = 60 * minute;
    private final long day = 24 * hour;

    private final TransactionLogRepo transactionLogRepo;
    private final NotificationService notificationService;
    private final TransactionLogService transactionLogService;

    @Scheduled(initialDelay = 5, fixedRate = 5 * minute, timeUnit = TimeUnit.SECONDS)
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
