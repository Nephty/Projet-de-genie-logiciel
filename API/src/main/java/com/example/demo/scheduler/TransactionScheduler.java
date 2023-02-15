package com.example.demo.scheduler;

import com.example.demo.model.TransactionLog;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler extends AbstractScheduler {

    private final TransactionLogRepo transactionLogRepo;
    private final TransactionLogService transactionLogService;
    private final Clock clock;


    /**
     * Fetch all the transactions to be performed in the DB and execute them
     * If a transaction can't be performed sends a notification to the user to prevent
     * him that the transaction hasn't gone through
     * Execute itself once per day except on the weekend
     */
    @Scheduled(initialDelay = 1, fixedRate = day, timeUnit = TimeUnit.SECONDS)
    public void performDueTransactions() {
        // Get the current date to know if we're in the weekend or not.
        // We use a clock because if we use Instant.now(), we can't mock it
        // and when we're testing we want to have control on the time in this case.
        LocalDate now = LocalDate.ofInstant(clock.instant(),clock.getZone());
        if(now.getDayOfWeek() == DayOfWeek.SUNDAY || now.getDayOfWeek() == DayOfWeek.SATURDAY) {
            log.info(now.getDayOfWeek().toString());
            return;
        }
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
