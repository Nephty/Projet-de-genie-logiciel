package com.example.demo.scheduler;

import com.example.demo.exception.throwables.AuthorizationException;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.CompositePK.AccountAccessPK;
import com.example.demo.model.TransactionLog;
import com.example.demo.other.Sender;
import com.example.demo.repository.NotificationRepo;
import com.example.demo.repository.TransactionLogRepo;
import com.example.demo.request.NotificationReq;
import com.example.demo.security.Role;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler {

    private final long minute = 60;
    private final long hour = 60 * minute;
    private final long day = 24 * hour;

    private final TransactionLogRepo transactionLogRepo;
    private final NotificationService notificationService;

    //@Scheduled(initialDelay = 5, fixedRate = day)
    public void performDueTransactions() {
        log.info("[SCHEDULED TASK] Performing transaction");
        ArrayList<TransactionLog> transactionsToPerform = transactionLogRepo
                .findAllByTransactionDateBeforeAndProcessedOrderByTransactionId(
                        new Date(System.currentTimeMillis()),false
                );
        for(int i = 0; i < transactionsToPerform.size(); i+= 2) {
            TransactionLog transactionA = transactionsToPerform.get(i);
            TransactionLog transactionB = transactionsToPerform.get(i + 1);
            assertFromSameTransfer(transactionA, transactionB);
            //the sending transaction has a direction of 1
            assertCanMakeTransaction(transactionA.getDirection() == 1 ? transactionA : transactionB);
        }
    }

    private void assertFromSameTransfer(TransactionLog transactionA, TransactionLog transactionB) {
        //Remove after making sure the query works
        if(transactionA.getTransactionDate().after(new Date(System.currentTimeMillis()))) {
            log.error("Do better SQL plz");
        }
        if(transactionA.getTransactionId().intValue() != transactionB.getTransactionId().intValue()
                || transactionA.getDirection().intValue() == transactionB.getDirection().intValue()
        ) {
            log.warn("[SCHEDULE]Error matching the id of transaction pair");
            transactionLogRepo.deleteAllByTransactionId(transactionA.getTransactionId());
            transactionLogRepo.deleteAllByTransactionId(transactionB.getTransactionId());
        }
    }

    private void assertCanMakeTransaction(TransactionLog transactionSent) {
        if(!transactionSent.getSubAccount().getIban().getPayment()) {
            log.warn("[SCHEDULE]This account can't make payment");
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            return;
        }
        if(transactionSent.getTransactionAmount() <= 0) {
            log.warn("[SCHEDULE]Can't make transaction lower or equal to 0");
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
            return;
        }
        if(transactionSent.getSubAccount().getCurrentBalance() < transactionSent.getTransactionAmount()) {
            log.warn("[SCHEDULE]Not enough fund");
            transactionLogRepo.deleteAllByTransactionId(transactionSent.getTransactionId());
        }
    }

    /**
     * Send a notification to the owner of the account that sent the notification.
     * It warns the user that his transaction couldn't be executed.
     * @param transactionSent The transaction that couldn't be executed.
     * @param reason The reason why the transaction couldn't be executed.
     */
    private void sendDeletedNotification(TransactionLog transactionSent,String reason){
        Sender bankSender = new Sender(
                transactionSent.getSubAccount().getIban().getSwift().getSwift(),
                Role.BANK
        );

        NotificationReq notification = new NotificationReq();
        notification.setNotificationType(5);
        notification.setComments(reason);
        notification.setStatus("UNCHECKED");
        notification.setRecipientId(transactionSent.getSubAccount().getIban().getUserId().getUserID());

        notificationService.addNotification(bankSender,notification);
    }

    //@Scheduled(initialDelay = 15, fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void testing() {
        log.info("SCHEDULE");
    }
}
