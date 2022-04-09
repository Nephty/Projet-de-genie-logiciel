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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component @Slf4j @RequiredArgsConstructor
public class TransactionScheduler extends AbstractScheduler {

    private final TransactionLogRepo transactionLogRepo;
    private final NotificationService notificationService;
    private final TransactionLogService transactionLogService;

    @Scheduled(initialDelay = 5, fixedRate = minute, timeUnit = TimeUnit.SECONDS)
    public void performDueTransactions() {
        ArrayList<TransactionLog> transactionsToPerform = transactionLogRepo.findAllToExecute();
        log.info("[SCHEDULED TASK] Performing transactions (n={})", transactionsToPerform.size());
        if(transactionsToPerform.size() % 2 != 0) {
            //Shouldn't be happening but if it happens we have to manage it correctly.
            ArrayList<TransactionLog> badFormatTransaction = transactionLogRepo.findBadFormatTransaction();
            for (TransactionLog t : badFormatTransaction){
                transactionLogRepo.deleteAllByTransactionId(t.getTransactionId());
                sendBadFormatTransaction(t);
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

    private void sendBadFormatTransaction(TransactionLog transaction){
        String reason = "An error occur with your transaction : ";
        reason += "We've lost the ";
        reason += transaction.getIsSender() ? "Receiver.\n" : "Sender.\n";
        reason += "To resolve that problem, we've deleted this Transaction.\n We apologise for the troubles\n";
        reason += "Transaction description : \n";
        reason += "Account : " + transaction.getSubAccount().getIban() + "\n";
        reason += "Date : " + transaction.getTransactionDate() + "\n";
        reason += "Amount :" + transaction.getTransactionAmount();


        Sender bankSender = new Sender(
                transaction.getSubAccount().getIban().getSwift().getSwift(),
                Role.BANK
        );

        NotificationReq notification = new NotificationReq();
        notification.setNotificationType(5);
        notification.setComments(reason);
        notification.setIsFlagged(true);
        notification.setRecipientId(transaction.getSubAccount().getIban().getUserId().getUserId());

        notificationService.addNotification(bankSender,notification);
    }

}
